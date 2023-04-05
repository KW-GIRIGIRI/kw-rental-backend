package com.girigiri.kwrental.inventory.repository;

import com.girigiri.kwrental.inventory.domain.Inventory;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static com.girigiri.kwrental.equipment.domain.QEquipment.equipment;
import static com.girigiri.kwrental.inventory.domain.QInventory.inventory;

public class InventoryRepositoryCustomImpl implements InventoryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public InventoryRepositoryCustomImpl(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    // TODO: 2023/04/06 회원으로 필터링 해야 한다.
    @Override
    public List<Inventory> findAllWithEquipment() {
        return jpaQueryFactory.selectFrom(inventory)
                .leftJoin(equipment).fetchJoin()
                .fetch();
    }
}
