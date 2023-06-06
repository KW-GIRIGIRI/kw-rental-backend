package com.girigiri.kwrental.asset.equipment.repository;

import static com.girigiri.kwrental.asset.equipment.domain.QEquipment.*;
import static com.girigiri.kwrental.util.QueryDSLUtils.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.girigiri.kwrental.asset.equipment.domain.Category;
import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class EquipmentRepositoryCustomImpl implements EquipmentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public EquipmentRepositoryCustomImpl(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<Equipment> findEquipmentBy(final Pageable pageable, final String keyword, final Category category) {
        final JPAQuery<Equipment> query = jpaQueryFactory.selectFrom(equipment)
                .where(
                        isContains(keyword, equipment.name),
                        isEqualTo(category, equipment.category)
                );
        setPageable(query, equipment, pageable);
        return new PageImpl<>(query.fetch(), pageable, countBy(query));
    }

    private long countBy(final JPAQuery<?> query) {
        final Long count = jpaQueryFactory.select(equipment.count())
                .from(equipment)
                .where(query.getMetadata().getWhere())
                .fetchOne();
        return count == null ? 0 : count;
    }
}
