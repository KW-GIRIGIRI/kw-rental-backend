package com.girigiri.kwrental.rental.repository;

import com.girigiri.kwrental.inventory.domain.RentalDateTime;
import com.girigiri.kwrental.labroom.domain.LabRoom;
import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import com.girigiri.kwrental.rental.dto.response.LabRoomReservationResponse;
import com.girigiri.kwrental.rental.dto.response.RentalSpecWithName;
import com.girigiri.kwrental.rental.repository.dto.RentalDto;
import com.girigiri.kwrental.rental.repository.dto.RentalSpecDto;
import com.girigiri.kwrental.rental.repository.dto.RentalSpecStatuesPerPropertyNumber;
import com.girigiri.kwrental.reservation.domain.ReservationSpecStatus;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.girigiri.kwrental.asset.domain.QRentableAsset.rentableAsset;
import static com.girigiri.kwrental.equipment.domain.QEquipment.equipment;
import static com.girigiri.kwrental.rental.domain.QAbstractRentalSpec.abstractRentalSpec;
import static com.girigiri.kwrental.rental.domain.QEquipmentRentalSpec.equipmentRentalSpec;
import static com.girigiri.kwrental.reservation.domain.QReservation.reservation;
import static com.girigiri.kwrental.reservation.domain.QReservationSpec.reservationSpec;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

public class RentalSpecRepositoryCustomImpl implements RentalSpecRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public RentalSpecRepositoryCustomImpl(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<EquipmentRentalSpec> findByPropertyNumbers(final Set<String> propertyNumbers) {
        return jpaQueryFactory.selectFrom(equipmentRentalSpec)
                .where(equipmentRentalSpec.propertyNumber.in(propertyNumbers))
                .fetch();
    }

    @Override
    public List<EquipmentRentalSpec> findByReservationSpecIds(final Set<Long> reservationSpecIds) {
        return jpaQueryFactory.selectFrom(equipmentRentalSpec)
                .where(equipmentRentalSpec.reservationSpecId.in(reservationSpecIds))
                .fetch();
    }

    @Override
    public Set<EquipmentRentalSpec> findRentedRentalSpecs(final Long equipmentId, final LocalDateTime dateTime) {
        return Set.copyOf(
                jpaQueryFactory.selectFrom(equipmentRentalSpec)
                        .leftJoin(reservationSpec).on(equipmentRentalSpec.reservationSpecId.eq(reservationSpec.id))
                        .where(reservationSpec.rentable.id.eq(equipmentId)
                                .and(equipmentRentalSpec.acceptDateTime.instant.loe(RentalDateTime.from(dateTime).getInstant())
                                        .and(equipmentRentalSpec.returnDateTime.isNull())))
                        .fetch());
    }

    @Override
    public List<EquipmentRentalSpec> findByReservationId(final Long reservationId) {
        return jpaQueryFactory.selectFrom(equipmentRentalSpec)
                .where(equipmentRentalSpec.reservationId.eq(reservationId))
                .fetch();
    }

    @Override
    public List<RentalDto> findRentalDtosBetweenDate(final Long memberId, final LocalDate from, final LocalDate to) {
        return jpaQueryFactory
                .from(abstractRentalSpec)
                .join(reservationSpec).on(reservationSpec.id.eq(abstractRentalSpec.reservationSpecId))
                .join(reservation).on(reservation.memberId.eq(memberId))
                .join(equipment).on(equipment.id.eq(reservationSpec.rentable.id))
                .where(reservationSpecBetweenDate(from, to))
                .transform(groupBy(reservationSpec.period)
                        .list(Projections.constructor(RentalDto.class, reservationSpec.period.rentalStartDate, reservationSpec.period.rentalEndDate,
                                GroupBy.set(Projections.constructor(RentalSpecDto.class, abstractRentalSpec.id, equipment.name, abstractRentalSpec.status)))));
    }

    private BooleanExpression reservationSpecBetweenDate(final LocalDate from, final LocalDate to) {
        return reservationSpec.period.rentalStartDate.goe(from).and(reservationSpec.period.rentalEndDate.loe(to));
    }

    @Override
    public List<RentalSpecStatuesPerPropertyNumber> findStatusesByPropertyNumbersBetweenDate(final Set<String> propertyNumbers, final LocalDate from, final LocalDate to) {
        final Map<String, List<RentalSpecStatus>> statusPerPropertyNumber = jpaQueryFactory
                .from(equipmentRentalSpec)
                .join(reservationSpec).on(reservationSpec.id.eq(equipmentRentalSpec.reservationSpecId).and(reservationSpecBetweenDate(from, to)))
                .where(equipmentRentalSpec.propertyNumber.in(propertyNumbers))
                .transform(groupBy(equipmentRentalSpec.propertyNumber)
                        .as(list(equipmentRentalSpec.status)));
        return statusPerPropertyNumber.entrySet().stream()
                .map(entry -> new RentalSpecStatuesPerPropertyNumber(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    public List<RentalSpecWithName> findTerminatedWithNameByPropertyNumber(final String propertyNumber) {
        return jpaQueryFactory
                .select(Projections.constructor(RentalSpecWithName.class, reservation.name, equipmentRentalSpec.acceptDateTime, equipmentRentalSpec.returnDateTime, equipmentRentalSpec.status))
                .from(equipmentRentalSpec)
                .join(reservation).on(reservation.id.eq(equipmentRentalSpec.reservationId), reservation.terminated.isTrue())
                .where(equipmentRentalSpec.propertyNumber.eq(propertyNumber))
                .fetch();
    }

    @Override
    public void updateNormalReturnedByReservationIds(final List<Long> reservationIds, final RentalDateTime returnDateTime) {
        jpaQueryFactory.update(abstractRentalSpec)
                .set(abstractRentalSpec.status, RentalSpecStatus.RETURNED)
                .set(abstractRentalSpec.returnDateTime, returnDateTime)
                .where(abstractRentalSpec.reservationId.in(reservationIds))
                .execute();
    }

    @Override
    public List<LabRoomReservationResponse> getLabRoomReservationWithRentalSpec(final String labRoomName, final LocalDate startDate) {
        return jpaQueryFactory
                .from(abstractRentalSpec)
                .join(reservation).on(reservation.id.eq(abstractRentalSpec.reservationId))
                .join(reservationSpec).on(reservationSpec.id.eq(abstractRentalSpec.reservationSpecId))
                .join(rentableAsset).on(rentableAsset.eq(reservationSpec.rentable))
                .where(
                        rentableAsset.instanceOf(LabRoom.class), rentableAsset.name.eq(labRoomName),
                        reservationSpec.period.rentalStartDate.eq(startDate), reservationSpec.status.in(ReservationSpecStatus.RETURNED, ReservationSpecStatus.ABNORMAL_RETURNED)
                )
                .select(Projections.constructor(LabRoomReservationResponse.class,
                        reservation.id, reservationSpec.id, reservationSpec.period.rentalStartDate,
                        reservationSpec.period.rentalEndDate, reservation.name, abstractRentalSpec.status))
                .fetch();
    }
}
