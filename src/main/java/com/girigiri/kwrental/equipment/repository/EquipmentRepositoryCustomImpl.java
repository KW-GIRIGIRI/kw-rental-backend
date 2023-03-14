package com.girigiri.kwrental.equipment.repository;

import static com.girigiri.kwrental.equipment.domain.QEquipment.equipment;
import static com.girigiri.kwrental.util.QueryDSLUtils.isContains;
import static com.girigiri.kwrental.util.QueryDSLUtils.setPageable;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class EquipmentRepositoryCustomImpl implements EquipmentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public EquipmentRepositoryCustomImpl(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<Equipment> findEquipmentBy(final Pageable pageable, final String keyword) {
        final Long count = jpaQueryFactory.select(equipment.id.count())
                .from(equipment)
                .where(isContains(keyword, equipment.modelName))
                .fetchOne();
        final JPAQuery<Equipment> query = jpaQueryFactory.selectFrom(equipment)
                .where(isContains(keyword, equipment.modelName));
        setPageable(query, equipment, pageable);
        final List<Equipment> equipments = query.fetch();
        return new PageImpl<>(equipments, pageable, count == null ? 0 : count);
    }
}
