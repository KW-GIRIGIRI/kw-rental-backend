package com.girigiri.kwrental.item.repository;

import static com.girigiri.kwrental.item.domain.QItem.item;

import com.querydsl.jpa.impl.JPAQueryFactory;

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
}
