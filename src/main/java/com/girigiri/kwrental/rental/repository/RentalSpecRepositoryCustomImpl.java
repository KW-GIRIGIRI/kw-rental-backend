package com.girigiri.kwrental.rental.repository;

import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.girigiri.kwrental.rental.domain.QRentalSpec.rentalSpec;
import static com.girigiri.kwrental.reservation.domain.QReservationSpec.reservationSpec;

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
}
