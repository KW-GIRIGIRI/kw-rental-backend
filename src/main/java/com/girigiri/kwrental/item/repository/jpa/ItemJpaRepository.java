package com.girigiri.kwrental.item.repository.jpa;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.girigiri.kwrental.item.domain.Item;

public interface ItemJpaRepository extends Repository<Item, Long>, ItemQueryDslRepositoryCustom, ItemJdbcRepositoryCustom {
    Optional<Item> findById(Long id);

    Item save(Item item);

    Optional<Item> findByPropertyNumber(String propertyNumber);
}