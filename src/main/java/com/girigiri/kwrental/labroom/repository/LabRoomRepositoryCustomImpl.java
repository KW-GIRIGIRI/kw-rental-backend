package com.girigiri.kwrental.labroom.repository;

import static com.girigiri.kwrental.labroom.domain.QLabRoom.*;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class LabRoomRepositoryCustomImpl implements LabRoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public LabRoomRepositoryCustomImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public void updateNotice(Long id, String content) {
        queryFactory.update(labRoom)
            .set(labRoom.notice, content)
            .where(labRoom.id.eq(id))
            .execute();
    }

    @Override
    public void updateAvailable(final Long id, final boolean available) {
        queryFactory.update(labRoom)
            .set(labRoom.isAvailable, available)
            .where(labRoom.id.eq(id))
            .execute();
    }
}
