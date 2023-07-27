package com.girigiri.kwrental.rental.repository;

import static com.girigiri.kwrental.asset.domain.QRentableAsset.*;
import static com.girigiri.kwrental.asset.equipment.domain.QEquipment.*;
import static com.girigiri.kwrental.asset.labroom.domain.QLabRoom.*;
import static com.girigiri.kwrental.rental.domain.QAbstractRentalSpec.*;
import static com.girigiri.kwrental.rental.domain.QEquipmentRentalSpec.*;
import static com.girigiri.kwrental.reservation.domain.entity.QReservation.*;
import static com.girigiri.kwrental.reservation.domain.entity.QReservationSpec.*;
import static com.girigiri.kwrental.util.QueryDSLUtils.*;
import static com.querydsl.core.group.GroupBy.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.rental.domain.AbstractRentalSpec;
import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import com.girigiri.kwrental.rental.dto.response.EquipmentRentalsDto.EquipmentRentalDto;
import com.girigiri.kwrental.rental.dto.response.LabRoomRentalsDto.LabRoomRentalDto;
import com.girigiri.kwrental.rental.dto.response.LabRoomReservationResponse;
import com.girigiri.kwrental.rental.dto.response.RentalSpecStatuesPerPropertyNumber;
import com.girigiri.kwrental.rental.dto.response.RentalSpecWithName;
import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpecStatus;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class RentalSpecRepositoryCustomImpl implements RentalSpecRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public RentalSpecRepositoryCustomImpl(final JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public List<EquipmentRentalSpec> findByPropertyNumbers(final Set<String> propertyNumbers) {
		return queryFactory.selectFrom(equipmentRentalSpec)
			.where(equipmentRentalSpec.propertyNumber.in(propertyNumbers))
			.fetch();
	}

	@Override
	public List<AbstractRentalSpec> findByReservationSpecIds(final Set<Long> reservationSpecIds) {
		return queryFactory.selectFrom(abstractRentalSpec)
			.where(abstractRentalSpec.reservationSpecId.in(reservationSpecIds))
			.fetch();
	}

	@Override
	public Set<EquipmentRentalSpec> findRentedRentalSpecsByAssetId(final Long equipmentId,
		final LocalDateTime dateTime) {
		return Set.copyOf(queryFactory.selectFrom(equipmentRentalSpec)
			.leftJoin(reservationSpec)
			.on(equipmentRentalSpec.reservationSpecId.eq(reservationSpec.id))
			.where(reservationSpec.rentable.id.eq(equipmentId)
				.and(equipmentRentalSpec.acceptDateTime.instant.loe(RentalDateTime.from(dateTime).getInstant())
					.and(equipmentRentalSpec.returnDateTime.isNull())))
			.fetch());
	}

	@Override
	public List<EquipmentRentalSpec> findByReservationId(final Long reservationId) {
		return queryFactory.selectFrom(equipmentRentalSpec)
			.where(equipmentRentalSpec.reservationId.eq(reservationId))
			.fetch();
	}

	@Override
	public List<EquipmentRentalDto> findEquipmentRentalDtosBetweenDate(final Long memberId, final LocalDate from,
		final LocalDate to) {
		return queryFactory.from(abstractRentalSpec)
			.join(reservationSpec)
			.on(reservationSpec.id.eq(abstractRentalSpec.reservationSpecId))
			.join(reservation)
			.on(abstractRentalSpec.reservationId.eq(reservation.id), reservation.memberId.eq(memberId))
			.join(equipment)
			.on(equipment.id.eq(reservationSpec.rentable.id))
			.where(reservationSpecBetweenDate(from, to))
			.transform(groupBy(reservationSpec.period).list(
				Projections.constructor(EquipmentRentalDto.class, reservationSpec.period.rentalStartDate,
					reservationSpec.period.rentalEndDate, GroupBy.set(
						Projections.constructor(EquipmentRentalDto.EquipmentRentalSpecDto.class, abstractRentalSpec.id,
							equipment.name,
							abstractRentalSpec.status)))));
	}

	@Override
	public List<LabRoomRentalDto> findLabRoomRentalDtosBetweenDate(final Long memberId, final LocalDate from,
		final LocalDate to) {
		return queryFactory.from(abstractRentalSpec)
			.join(reservationSpec)
			.on(reservationSpec.id.eq(abstractRentalSpec.reservationSpecId))
			.join(reservation)
			.on(abstractRentalSpec.reservationId.eq(reservation.id), reservation.memberId.eq(memberId))
			.join(labRoom)
			.on(labRoom.id.eq(reservationSpec.rentable.id))
			.where(reservationSpecBetweenDate(from, to))
			.select(Projections.constructor(LabRoomRentalDto.class, reservationSpec.period.rentalStartDate,
				reservationSpec.period.rentalEndDate, labRoom.name, reservationSpec.amount.amount,
				abstractRentalSpec.status))
			.fetch();
	}

	private BooleanExpression reservationSpecBetweenDate(final LocalDate from, final LocalDate to) {
		return reservationSpec.period.rentalStartDate.goe(from).and(reservationSpec.period.rentalEndDate.loe(to));
	}

	@Override
	public List<RentalSpecStatuesPerPropertyNumber> findStatusesByPropertyNumbersBetweenDate(
		final Set<String> propertyNumbers, final LocalDate from, final LocalDate to) {
		final Map<String, List<RentalSpecStatus>> statusPerPropertyNumber = queryFactory.from(equipmentRentalSpec)
			.join(reservationSpec)
			.on(reservationSpec.id.eq(equipmentRentalSpec.reservationSpecId).and(reservationSpecBetweenDate(from, to)))
			.where(equipmentRentalSpec.propertyNumber.in(propertyNumbers))
			.transform(groupBy(equipmentRentalSpec.propertyNumber).as(list(equipmentRentalSpec.status)));
		return statusPerPropertyNumber.entrySet()
			.stream()
			.map(entry -> new RentalSpecStatuesPerPropertyNumber(entry.getKey(), entry.getValue()))
			.toList();
	}

	@Override
	public List<RentalSpecWithName> findTerminatedWithNameByPropertyNumber(final String propertyNumber) {
		return getReturnedEquipmentRentalSpecsWithName(equipmentRentalSpec.propertyNumber.eq(propertyNumber));
	}

	@Override
	public List<RentalSpecWithName> findTerminatedWithNameByPropertyAndInclusive(final String propertyNumber,
		final RentalDateTime startDate, final RentalDateTime endDate) {
		return getReturnedEquipmentRentalSpecsWithName(
			equipmentRentalSpec.propertyNumber.eq(propertyNumber),
			equipmentRentalSpec.returnDateTime.instant.lt(endDate.calculateDay(1).getInstant()),
			equipmentRentalSpec.acceptDateTime.instant.goe(startDate.getInstant()));
	}

	private List<RentalSpecWithName> getReturnedEquipmentRentalSpecsWithName(final Predicate... predicates) {
		return queryFactory.select(
				Projections.constructor(RentalSpecWithName.class, reservation.name, equipmentRentalSpec.acceptDateTime,
					equipmentRentalSpec.returnDateTime, equipmentRentalSpec.status))
			.from(equipmentRentalSpec)
			.join(reservation)
			.on(reservation.id.eq(equipmentRentalSpec.reservationId), reservation.terminated.isTrue())
			.where(predicates)
			.fetch();
	}

	@Override
	public void updateNormalReturnedByReservationIds(final List<Long> reservationIds,
		final RentalDateTime returnDateTime) {
		queryFactory.update(abstractRentalSpec)
			.set(abstractRentalSpec.status, RentalSpecStatus.RETURNED)
			.set(abstractRentalSpec.returnDateTime, returnDateTime)
			.where(abstractRentalSpec.reservationId.in(reservationIds))
			.execute();
	}

	@Override
	public List<LabRoomReservationResponse> getReturnedLabRoomReservationResponse(final String labRoomName,
		final LocalDate startDate) {
		return queryFactory.from(abstractRentalSpec)
			.join(reservation)
			.on(reservation.id.eq(abstractRentalSpec.reservationId))
			.join(reservationSpec)
			.on(reservationSpec.id.eq(abstractRentalSpec.reservationSpecId))
			.join(rentableAsset)
			.on(rentableAsset.eq(reservationSpec.rentable))
			.where(rentableAsset.instanceOf(LabRoom.class), rentableAsset.name.eq(labRoomName),
				reservationSpec.status.in(ReservationSpecStatus.RETURNED, ReservationSpecStatus.ABNORMAL_RETURNED),
				reservationSpec.period.rentalStartDate.eq(startDate))
			.select(Projections.constructor(LabRoomReservationResponse.class, reservation.id, reservationSpec.id,
				reservationSpec.period.rentalStartDate, reservationSpec.period.rentalEndDate, reservation.name,
				abstractRentalSpec.status))
			.fetch();
	}

	@Override
	public Page<LabRoomReservationResponse> getReturnedLabRoomReservationResponse(final String labRoomName,
		final LocalDate startDate, final LocalDate endDate, final Pageable pageable) {
		JPAQuery<LabRoomReservationResponse> query = queryFactory.from(abstractRentalSpec)
			.join(reservation)
			.on(reservation.id.eq(abstractRentalSpec.reservationId))
			.join(reservationSpec)
			.on(reservationSpec.id.eq(abstractRentalSpec.reservationSpecId))
			.join(rentableAsset)
			.on(rentableAsset.eq(reservationSpec.rentable))
			.where(rentableAsset.instanceOf(LabRoom.class), rentableAsset.name.eq(labRoomName),
				reservationSpec.status.in(ReservationSpecStatus.RETURNED, ReservationSpecStatus.ABNORMAL_RETURNED),
				reservationSpec.period.rentalStartDate.goe(startDate),
				reservationSpec.period.rentalEndDate.loe(endDate))
			.select(Projections.constructor(LabRoomReservationResponse.class, reservation.id, reservationSpec.id,
				reservationSpec.period.rentalStartDate, reservationSpec.period.rentalEndDate, reservation.name,
				abstractRentalSpec.status));

		setPageable(query, abstractRentalSpec, pageable);
		return new PageImpl<>(query.fetch(), pageable, countBy(query));
	}

	@Override
	public void updatePropertyNumber(String from, String to) {
		queryFactory.update(equipmentRentalSpec)
			.set(equipmentRentalSpec.propertyNumber, to)
			.where(equipmentRentalSpec.propertyNumber.eq(from))
			.execute();
	}

	@Override
	public List<AbstractRentalSpec> findRentedRentalSpecsByAssetId(Long assetId) {
		return queryFactory.selectFrom(abstractRentalSpec)
			.join(reservationSpec)
			.on(reservationSpec.id.eq(abstractRentalSpec.reservationSpecId))
			.where(reservationSpec.rentable.id.eq(assetId), abstractRentalSpec.status.eq(RentalSpecStatus.RENTED))
			.fetch();
	}

	@Override
	public List<EquipmentRentalSpec> findRentedRentalSpecsByPropertyNumber(final String propertyNumber) {
		return queryFactory.selectFrom(equipmentRentalSpec)
			.where(equipmentRentalSpec.propertyNumber.eq(propertyNumber),
				equipmentRentalSpec.status.eq(RentalSpecStatus.RENTED))
			.fetch();
	}

	@Override
	public List<AbstractRentalSpec> findNowRentedRentalSpecsByName(final String name) {
		final LocalDate now = LocalDate.now();
		return queryFactory.selectFrom(abstractRentalSpec)
			.leftJoin(reservationSpec).on(reservationSpec.id.eq(abstractRentalSpec.reservationSpecId))
			.leftJoin(rentableAsset).on(rentableAsset.id.eq(reservationSpec.rentable.id))
			.where(rentableAsset.name.eq(name), reservationSpec.period.rentalStartDate.loe(now),
				reservationSpec.period.rentalEndDate.after(now), abstractRentalSpec.status.eq(RentalSpecStatus.RENTED))
			.fetch();
	}

	private long countBy(final JPAQuery<?> query) {
		final Long count = queryFactory.select(abstractRentalSpec.count())
			.from(abstractRentalSpec)
			.join(reservationSpec)
			.on(reservationSpec.id.eq(abstractRentalSpec.reservationSpecId))
			.join(rentableAsset)
			.on(rentableAsset.eq(reservationSpec.rentable))
			.where(query.getMetadata().getWhere())
			.fetchOne();
		return count == null ? 0 : count;
	}
}
