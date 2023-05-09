package com.girigiri.kwrental.item.repository;

import com.girigiri.kwrental.item.domain.Item;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.girigiri.kwrental.item.domain.QItem.item;

public class ItemQueryDslRepositoryCustomImpl implements ItemQueryDslRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ItemQueryDslRepositoryCustomImpl(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public int updateRentalAvailable(final Long id, final boolean available) {
        return (int) jpaQueryFactory.update(item)
                .where(item.id.eq(id))
                .set(item.available, available)
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
                .where(item.equipmentId.eq(equipmentId).and(item.available.isTrue()))
                .fetchOne()).intValue();
    }

    @Override
    public List<Item> findByEquipmentIds(final Set<Long> equipmentIds) {
        return jpaQueryFactory.selectFrom(item)
                .where(item.equipmentId.in(equipmentIds))
                .fetch();
    }

    @Override
    public long deleteByPropertyNumbers(final List<String> propertyNumbers) {
        return jpaQueryFactory.delete(item)
                .where(item.propertyNumber.in(propertyNumbers))
                .execute();
    }
}
