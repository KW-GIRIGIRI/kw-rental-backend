package com.girigiri.kwrental.asset.repository;

import static com.girigiri.kwrental.asset.domain.QRentableAsset.*;

import java.time.LocalDate;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AssetRepositoryCustomImpl implements AssetRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public void deleteById(final Long id) {
		queryFactory.update(rentableAsset)
			.set(rentableAsset.deletedAt, LocalDate.now())
			.where(rentableAsset.id.eq(id))
			.execute();
	}
}
