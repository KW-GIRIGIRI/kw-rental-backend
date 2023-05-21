package com.girigiri.kwrental.labroom.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class LabRoomRepositoryCustomImpl implements LabRoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public LabRoomRepositoryCustomImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }
}
