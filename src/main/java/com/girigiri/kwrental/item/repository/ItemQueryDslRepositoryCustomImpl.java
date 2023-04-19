package com.girigiri.kwrental.item.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.Objects;

import static com.girigiri.kwrental.item.domain.QItem.item;

public class ItemQueryDslRepositoryCustomImpl implements ItemQueryDslRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ItemQueryDslRepositoryCustomImpl(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public int updateRentalAvailable(final Long id, final boolean rentalAvailable) {
        return (int) jpaQueryFactory.update(item)
                .where(item.id.eq(id))
                .set(item.rentalAvailable, rentalAvailable)
                .execute();
    }

    @Override
    public int updatePropertyNumber(final Long id, final String propertyNumber) {
        return (int) jpaQueryFactory.update(item)
                .where(item.id.eq(id))
                .set(item.propertyNumber, propertyNumber)
                .execute();
    }

    @Override
    public int countAvailable(final Long equipmentId) {
        return Objects.requireNonNull(jpaQueryFactory.select(item.count())
                .from(item)
                .where(item.equipmentId.eq(equipmentId).and(item.rentalAvailable.isTrue()))
                .fetchOne()).intValue();
    }
}
