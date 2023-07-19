package com.girigiri.kwrental.reservation.repository;

import static com.girigiri.kwrental.asset.domain.QRentableAsset.*;
import static com.girigiri.kwrental.asset.equipment.domain.QEquipment.*;
import static com.girigiri.kwrental.auth.domain.QMember.*;
import static com.girigiri.kwrental.reservation.domain.entity.QReservation.*;
import static com.girigiri.kwrental.reservation.domain.entity.QReservationSpec.*;
import static com.querydsl.core.group.GroupBy.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpecStatus;
import com.girigiri.kwrental.reservation.domain.entity.ReservedAmount;
import com.girigiri.kwrental.reservation.dto.response.HistoryStatResponse;
import com.girigiri.kwrental.reservation.dto.response.LabRoomReservationSpecWithMemberNumberResponse;
import com.girigiri.kwrental.reservation.dto.response.LabRoomReservationWithMemberNumberResponse;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReservationSpecRepositoryCustomImpl implements ReservationSpecRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final EntityManager entityManager;

	@Override
	public List<ReservationSpec> findOverlappedReservedOrRentedInclusive(final Long rentableId, final LocalDate start,
		final LocalDate end) {
		return findOverlappedReservedOrRentedByPeriod(rentableId, new RentalPeriod(start, end.plusDays(1)));
	}

	@Override
	public List<ReservationSpec> findOverlappedReservedOrRentedByPeriod(final Long rentableId,
		final RentalPeriod rentalPeriod) {
		final LocalDate start = rentalPeriod.getRentalStartDate();
		final LocalDate end = rentalPeriod.getRentalEndDate();
		final List<ReservationSpec> overlappedLeft = queryFactory.selectFrom(reservationSpec)
			.where(reservationSpec.rentable.id.eq(rentableId),
				reservationSpec.status.in(ReservationSpecStatus.RESERVED, ReservationSpecStatus.RETURNED),
				reservationSpec.period.rentalStartDate.loe(start),
				reservationSpec.period.rentalEndDate.after(start))
			.fetch();
		final List<ReservationSpec> overLappedRight = queryFactory.selectFrom(reservationSpec)
			.where(reservationSpec.rentable.id.eq(rentableId),
				reservationSpec.status.in(ReservationSpecStatus.RESERVED, ReservationSpecStatus.RETURNED),
				reservationSpec.period.rentalStartDate.after(start),
				reservationSpec.period.rentalStartDate.before(end))
			.fetch();
		return Stream.concat(overlappedLeft.stream(), overLappedRight.stream())
			.distinct().toList();
	}

	@Override
	public List<ReservedAmount> findRentalAmountsByAssetIds(final List<Long> assetIds, final LocalDate date) {
		return queryFactory
			.select(Projections.constructor(ReservedAmount.class, equipment.id, equipment.rentableQuantity,
				reservationSpec.amount.amount.sum().coalesce(0)))
			.from(reservationSpec)
			.rightJoin(equipment).on(reservationSpec.rentable.id.eq(equipment.id).
				and(reservationSpec.period.rentalStartDate.loe(date))
				.and(reservationSpec.period.rentalEndDate.after(date)))
			.where(equipment.id.in(assetIds))
			.groupBy(equipment.id)
			.fetch();
	}

	@Override
	public List<ReservationSpec> findNotCanceldByStartDateBetween(final Long equipmentId, final LocalDate start,
		final LocalDate end) {
		return queryFactory
			.selectFrom(reservationSpec)
			.leftJoin(reservationSpec.reservation).fetchJoin()
			.where(
				reservationSpec.rentable.id.eq(equipmentId), reservationSpec.status.ne(ReservationSpecStatus.CANCELED)
					.and(reservationSpec.period.rentalStartDate.goe(start))
					.and(reservationSpec.period.rentalStartDate.loe(end)))
			.fetch();
	}

	@Override
	// TODO: 2023/06/01 양방향 detach문제 해결해야 한다.
	public void adjustAmountAndStatus(final ReservationSpec reservationSpecForUpdate) {
		//        entityManager.detach(reservationSpecForUpdate);
		queryFactory.update(reservationSpec)
			.set(reservationSpec.amount, reservationSpecForUpdate.getAmount())
			.set(reservationSpec.status, reservationSpecForUpdate.getStatus())
			.where(reservationSpec.id.eq(reservationSpecForUpdate.getId()))
			.execute();
		entityManager.merge(reservationSpecForUpdate);
	}

	@Override
	public Set<EquipmentReservationWithMemberNumber> findEquipmentReservationWhenAccept(final LocalDate date) {
		return findEquipmentReservationWhere(
			reservationSpec.rentable.instanceOf(Equipment.class),
			reservationSpec.status.in(ReservationSpecStatus.RESERVED, ReservationSpecStatus.RENTED),
			reservationSpec.period.rentalStartDate.eq(date)
		);
	}

	private Set<EquipmentReservationWithMemberNumber> findEquipmentReservationWhere(final Predicate... predicates) {
		return Set.copyOf(queryFactory
			.from(reservationSpec)
			.leftJoin(reservation).on(reservationSpec.reservation.id.eq(reservation.id))
			.leftJoin(reservationSpec.rentable).fetchJoin()
			.leftJoin(member).on(member.id.eq(reservation.memberId))
			.where(predicates)
			.transform(groupBy(reservation.id)
				.list(Projections.constructor(EquipmentReservationWithMemberNumber.class,
					reservation.id, reservation.name, member.memberNumber, reservation.acceptDateTime,
					list(reservationSpec)))
			)
		);
	}

	@Override
	public Set<EquipmentReservationWithMemberNumber> findOverdueEquipmentReservationWhenReturn(final LocalDate date) {
		return findEquipmentReservationWhere(
			reservationSpec.rentable.instanceOf(Equipment.class),
			reservationSpec.status.eq(ReservationSpecStatus.OVERDUE_RENTED),
			reservationSpec.period.rentalEndDate.before(date)
		);
	}

	@Override
	public Set<EquipmentReservationWithMemberNumber> findEquipmentReservationWhenReturn(final LocalDate date) {
		return findEquipmentReservationWhere(
			reservationSpec.rentable.instanceOf(Equipment.class),
			reservationSpec.status.eq(ReservationSpecStatus.RENTED),
			reservationSpec.period.rentalEndDate.eq(date)
		);
	}

	@Override
	public Set<LabRoomReservationWithMemberNumberResponse> findLabRoomReservationsWhenAccept(final LocalDate date) {
		return findLabRoomReservationsWhere(
			reservationSpec.rentable.instanceOf(LabRoom.class),
			reservationSpec.status.in(ReservationSpecStatus.RESERVED, ReservationSpecStatus.RENTED),
			reservationSpec.period.rentalStartDate.eq(date));
	}

	@Override
	public Set<LabRoomReservationWithMemberNumberResponse> findLabRoomReservationWhenReturn(final LocalDate date) {
		return findLabRoomReservationsWhere(
			reservationSpec.rentable.instanceOf(LabRoom.class),
			reservationSpec.status.in(ReservationSpecStatus.RENTED),
			reservationSpec.period.rentalEndDate.eq(date));
	}

	@Override
	public void updateStatusByIds(final List<Long> ids, final ReservationSpecStatus status) {
		queryFactory.update(reservationSpec)
			.set(reservationSpec.status, status)
			.where(reservationSpec.id.in(ids))
			.execute();
	}

	@Override
	public HistoryStatResponse findHistoryStat(String name, LocalDate startDate, LocalDate endDate) {
		int abnormalCount = Objects.requireNonNull(queryFactory.select(reservationSpec.count())
			.from(reservationSpec)
			.join(rentableAsset).on(rentableAsset.id.eq(reservationSpec.rentable.id), rentableAsset.name.eq(name))
			.where(reservationSpec.period.rentalStartDate.goe(startDate),
				reservationSpec.period.rentalEndDate.loe(endDate),
				reservationSpec.status.eq(ReservationSpecStatus.ABNORMAL_RETURNED))
			.fetchOne()).intValue();

		return queryFactory
			.from(reservationSpec)
			.join(rentableAsset).on(rentableAsset.id.eq(reservationSpec.rentable.id), rentableAsset.name.eq(name))
			.where(reservationSpec.period.rentalStartDate.goe(startDate),
				reservationSpec.period.rentalEndDate.loe(endDate),
				reservationSpec.status.in(ReservationSpecStatus.RETURNED, ReservationSpecStatus.ABNORMAL_RETURNED))
			.select(
				Projections.constructor(HistoryStatResponse.class, rentableAsset.name,
					reservationSpec.count().intValue(),
					reservationSpec.amount.amount.sum(),
					Expressions.constant(abnormalCount)))
			.fetchOne();
	}

	@Override
	public List<ReservationSpec> findReservedOrRentedByAssetId(Long assetId) {
		return queryFactory.selectFrom(reservationSpec)
			.join(reservation).fetchJoin()
			.where(reservationSpec.rentable.id.eq(assetId),
				reservationSpec.status.in(ReservationSpecStatus.RESERVED, ReservationSpecStatus.RENTED))
			.fetch();
	}

	private Set<LabRoomReservationWithMemberNumberResponse> findLabRoomReservationsWhere(
		final Predicate... predicates) {
		return Set.copyOf(
			queryFactory
				.from(reservationSpec)
				.leftJoin(reservation).on(reservationSpec.reservation.id.eq(reservation.id))
				.leftJoin(rentableAsset).on(reservationSpec.rentable.id.eq(rentableAsset.id))
				.leftJoin(member).on(member.id.eq(reservation.memberId))
				.where(predicates)
				.transform(groupBy(rentableAsset.id)
					.as(Projections.constructor(LabRoomReservationWithMemberNumberResponse.class,
						rentableAsset.name, reservation.acceptDateTime,
						list(Projections.constructor(LabRoomReservationSpecWithMemberNumberResponse.class,
							reservationSpec.id, reservationSpec.reservation.id, reservation.name, member.memberNumber,
							reservationSpec.amount.amount, reservation.phoneNumber)))))
				.values()
		);
	}
}
