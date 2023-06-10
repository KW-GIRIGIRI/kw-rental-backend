package com.girigiri.kwrental.asset.labroom.repository;

import static com.girigiri.kwrental.asset.labroom.domain.QLabRoomDailyBan.*;

import java.time.LocalDate;
import java.util.List;

import com.girigiri.kwrental.asset.labroom.domain.LabRoomDailyBan;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class LabRoomDailyBanRepositoryCustomImpl implements LabRoomDailyBanRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public LabRoomDailyBanRepositoryCustomImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public List<LabRoomDailyBan> findByLabRoomIdAndInclusive(Long labRoomId, LocalDate from, LocalDate to) {
		return queryFactory.selectFrom(labRoomDailyBan)
			.where(labRoomDailyBan.labRoomId.eq(labRoomId), labRoomDailyBan.banDate.loe(to),
				labRoomDailyBan.banDate.goe(from))
			.fetch();
	}
}
