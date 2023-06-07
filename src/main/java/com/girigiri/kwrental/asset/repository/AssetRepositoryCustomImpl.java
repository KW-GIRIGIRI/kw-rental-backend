package com.girigiri.kwrental.asset.repository;

import static com.girigiri.kwrental.asset.domain.QRentableAsset.*;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class AssetRepositoryCustomImpl implements AssetRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public AssetRepositoryCustomImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public void updateRentableQuantity(Long id, int rentableQuantity) {
		queryFactory.update(rentableAsset)
			.set(rentableAsset.rentableQuantity, rentableQuantity)
			.execute();
	}
}
