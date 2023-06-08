package com.girigiri.kwrental.asset.repository;

import static com.girigiri.kwrental.asset.domain.QRentableAsset.*;

import java.time.LocalDate;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class AssetRepositoryCustomImpl implements AssetRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public AssetRepositoryCustomImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public void updateRentableQuantity(final Long id, final int rentableQuantity) {
		queryFactory.update(rentableAsset)
			.set(rentableAsset.rentableQuantity, rentableQuantity)
			.execute();
	}

	@Override
	public void deleteById(final Long id) {
		queryFactory.update(rentableAsset)
			.set(rentableAsset.deletedAt, LocalDate.now())
			.where(rentableAsset.id.eq(id))
			.execute();
	}
}
