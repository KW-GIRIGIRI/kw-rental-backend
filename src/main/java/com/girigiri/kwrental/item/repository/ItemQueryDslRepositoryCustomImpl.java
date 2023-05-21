package com.girigiri.kwrental.item.repository;

import com.girigiri.kwrental.equipment.domain.Category;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.response.EquipmentItemDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.girigiri.kwrental.equipment.domain.QEquipment.equipment;
import static com.girigiri.kwrental.item.domain.QItem.item;
import static com.girigiri.kwrental.util.QueryDSLUtils.isEqualTo;
import static com.girigiri.kwrental.util.QueryDSLUtils.setPageable;

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

    @Override
    public Page<EquipmentItemDto> findEquipmentItem(final Pageable pageable, final Category category) {
        final JPAQuery<EquipmentItemDto> query = jpaQueryFactory.select(Projections.constructor(EquipmentItemDto.class, equipment.name, equipment.category, item.propertyNumber))
                .from(item)
                .join(equipment).on(equipment.id.eq(item.equipmentId))
                .where(isEqualTo(category, equipment.category));
        setPageable(query, item, pageable);
        return new PageImpl<>(query.fetch(), pageable, countBy(query));
    }

    private long countBy(final JPAQuery<?> query) {
        final Long count = jpaQueryFactory.select(item.count())
                .from(item)
                .join(equipment).on(equipment.id.eq(item.equipmentId))
                .where(query.getMetadata().getWhere())
                .fetchOne();
        return count == null ? 0 : count;
    }
}
