package com.girigiri.kwrental.rental.repository;

import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Set;

import static com.girigiri.kwrental.rental.domain.QRentalSpec.rentalSpec;

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
}
