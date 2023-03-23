package com.girigiri.kwrental.equipment.repository;

import static com.girigiri.kwrental.equipment.domain.QEquipment.equipment;
import static com.girigiri.kwrental.util.QueryDSLUtils.isContains;
import static com.girigiri.kwrental.util.QueryDSLUtils.isEqualTo;
import static com.girigiri.kwrental.util.QueryDSLUtils.setPageable;

import com.girigiri.kwrental.equipment.domain.Category;
import com.girigiri.kwrental.equipment.domain.Equipment;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class EquipmentRepositoryCustomImpl implements EquipmentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public EquipmentRepositoryCustomImpl(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<Equipment> findEquipmentBy(final Pageable pageable, final String keyword, final Category category) {
        final long count = countBy(
                isContains(keyword, equipment.modelName),
                isEqualTo(category, equipment.category)
        );

        final JPAQuery<Equipment> query = jpaQueryFactory.selectFrom(equipment)
                .where(
                        isContains(keyword, equipment.modelName),
                        isEqualTo(category, equipment.category)
                );
        setPageable(query, equipment, pageable);
        return new PageImpl<>(query.fetch(), pageable, count);
    }

    private long countBy(final Predicate... predicates) {
        final Long count = jpaQueryFactory.select(equipment.id.count())
                .from(equipment)
                .where(predicates)
                .fetchOne();
        return count == null ? 0 : count;
    }
}
