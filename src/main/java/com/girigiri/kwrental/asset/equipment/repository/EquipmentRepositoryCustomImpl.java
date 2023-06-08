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

	private final JPAQueryFactory queryFactory;

	public EquipmentRepositoryCustomImpl(final JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public Page<Equipment> findEquipmentBy(final Pageable pageable, final String keyword, final Category category) {
		final JPAQuery<Equipment> query = queryFactory.selectFrom(equipment)
			.where(
				isContains(keyword, equipment.name),
				isEqualTo(category, equipment.category),
				equipment.deletedAt.isNull()
			);
		setPageable(query, equipment, pageable);
		return new PageImpl<>(query.fetch(), pageable, countBy(query));
	}

	private long countBy(final JPAQuery<?> query) {
		final Long count = queryFactory.select(equipment.count())
			.from(equipment)
			.where(query.getMetadata().getWhere())
			.fetchOne();
		return count == null ? 0 : count;
	}
}
