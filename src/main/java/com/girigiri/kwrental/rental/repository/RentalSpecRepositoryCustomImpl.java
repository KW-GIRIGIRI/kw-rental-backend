package com.girigiri.kwrental.rental.repository;

import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.rental.repository.dto.RentalDto;
import com.girigiri.kwrental.rental.repository.dto.RentalSpecDto;
import com.girigiri.kwrental.rental.repository.dto.RentalSpecStatuesPerPropertyNumber;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.girigiri.kwrental.equipment.domain.QEquipment.equipment;
import static com.girigiri.kwrental.rental.domain.QRentalSpec.rentalSpec;
import static com.girigiri.kwrental.reservation.domain.QReservation.reservation;
import static com.girigiri.kwrental.reservation.domain.QReservationSpec.reservationSpec;
import static com.querydsl.core.group.GroupBy.groupBy;

public class RentalSpecRepositoryCustomImpl implements RentalSpecRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public RentalSpecRepositoryCustomImpl(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<RentalSpec> findByPropertyNumbers(final Set<String> propertyNumbers) {
        return jpaQueryFactory.selectFrom(rentalSpec)
                .where(rentalSpec.propertyNumber.in(propertyNumbers))
                .fetch();
    }

    @Override
    public List<RentalSpec> findByReservationSpecIds(final Set<Long> reservationSpecIds) {
        return jpaQueryFactory.selectFrom(rentalSpec)
                .where(rentalSpec.reservationSpecId.in(reservationSpecIds))
                .fetch();
    }

    @Override
    public Set<RentalSpec> findRentedRentalSpecs(final Long equipmentId, final LocalDateTime dateTime) {
        return Set.copyOf(
                jpaQueryFactory.selectFrom(rentalSpec)
                        .leftJoin(reservationSpec).on(rentalSpec.reservationSpecId.eq(reservationSpec.id))
                        .where(reservationSpec.equipment.id.eq(equipmentId)
                                .and(rentalSpec.acceptDateTime.loe(dateTime))
                                .and(rentalSpec.returnDateTime.isNull()))
                        .fetch());
    }

    @Override
    public List<RentalSpec> findByReservationId(final Long reservationId) {
        return jpaQueryFactory.selectFrom(rentalSpec)
                .where(rentalSpec.reservationId.eq(reservationId))
                .fetch();
    }

    @Override
    public List<RentalDto> findRentalDtosBetweenDate(final Long memberId, final LocalDate from, final LocalDate to) {
        return jpaQueryFactory
                .from(rentalSpec)
                .join(reservationSpec).on(reservationSpec.id.eq(rentalSpec.reservationSpecId))
                .join(reservation).on(reservation.memberId.eq(memberId))
                .join(equipment).on(equipment.id.eq(reservationSpec.equipment.id))
                .where(reservationSpecBetweenDate(from, to))
                .transform(groupBy(reservationSpec.period).
                        list(Projections.constructor(RentalDto.class, reservationSpec.period.rentalStartDate, reservationSpec.period.rentalEndDate,
                                GroupBy.set(Projections.constructor(RentalSpecDto.class, equipment.modelName, rentalSpec.status)))));
    }

    private BooleanExpression reservationSpecBetweenDate(final LocalDate from, final LocalDate to) {
        return reservationSpec.period.rentalStartDate.goe(from).and(reservationSpec.period.rentalEndDate.loe(to));
    }

    @Override
    public List<RentalSpecStatuesPerPropertyNumber> findStatusesByPropertyNumbersBetweenDate(final Set<String> propertyNumbers, final LocalDate from, final LocalDate to) {
        return jpaQueryFactory
                .select(Projections.constructor(RentalSpecStatuesPerPropertyNumber.class,
                        rentalSpec.propertyNumber,
                        Projections.list(rentalSpec.status)))
                .from(rentalSpec)
                .join(reservationSpec).on(reservationSpec.id.eq(rentalSpec.reservationSpecId).and(reservationSpecBetweenDate(from, to)))
                .where(rentalSpec.propertyNumber.in(propertyNumbers))
                .fetch();
    }
}
