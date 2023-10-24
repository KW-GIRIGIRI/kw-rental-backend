package com.girigiri.kwrental.item.repository;

import static com.girigiri.kwrental.asset.equipment.domain.QEquipment.*;
import static com.girigiri.kwrental.item.domain.QItem.*;
import static com.girigiri.kwrental.util.QueryDSLUtils.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.girigiri.kwrental.asset.equipment.domain.Category;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.response.EquipmentItemDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemQueryDslRepositoryCustomImpl implements ItemQueryDslRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public int countAvailable(final Long equipmentId) {
		return Objects.requireNonNull(queryFactory.select(item.count())
			.from(item)
			.where(item.assetId.eq(equipmentId), item.available.isTrue(), item.deletedAt.isNull())
			.fetchOne()).intValue();
	}

	@Override
	public List<Item> findByEquipmentIds(final Set<Long> equipmentIds) {
		return queryFactory.selectFrom(item)
			.where(item.assetId.in(equipmentIds), item.deletedAt.isNull())
			.fetch();
	}

	@Override
	public int deleteByPropertyNumbers(final List<String> propertyNumbers) {
		return (int)queryFactory.update(item)
			.set(item.deletedAt, LocalDate.now())
			.where(item.propertyNumber.in(propertyNumbers))
			.execute();
	}

	@Override
	public Page<EquipmentItemDto> findEquipmentItem(final Pageable pageable, final Category category) {
		final JPAQuery<EquipmentItemDto> query = queryFactory.select(
				Projections.constructor(EquipmentItemDto.class, equipment.name, equipment.category, item.propertyNumber))
			.from(item)
			.join(equipment).on(equipment.id.eq(item.assetId))
			.where(isEqualTo(category, equipment.category), item.deletedAt.isNull());
		setPageable(query, item, pageable);
		return new PageImpl<>(query.fetch(), pageable, countBy(query));
	}

	@Override
	public int updateAvailable(List<Long> ids, boolean available) {
		return (int)queryFactory.update(item)
			.set(item.available, available)
			.where(item.id.in(ids))
			.execute();
	}

	private long countBy(final JPAQuery<?> query) {
		final Long count = queryFactory.select(item.count())
			.from(item)
			.join(equipment).on(equipment.id.eq(item.assetId))
			.where(query.getMetadata().getWhere())
			.fetchOne();
		return count == null ? 0 : count;
	}

	@Override
	public int deleteByIdIn(final Collection<Long> ids) {
		return (int)queryFactory.update(item)
			.set(item.deletedAt, LocalDate.now())
			.where(item.id.in(ids), item.deletedAt.isNull())
			.execute();
	}

	@Override
	public int deleteById(Long id) {
		return (int)queryFactory.update(item)
			.set(item.deletedAt, LocalDate.now())
			.where(item.id.eq(id))
			.execute();
	}

	@Override
	public List<Item> findByAssetId(Long assetId) {
		return queryFactory.selectFrom(item)
			.where(item.assetId.eq(assetId), item.deletedAt.isNull())
			.fetch();
	}
}
