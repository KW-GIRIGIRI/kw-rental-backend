package com.girigiri.kwrental.item.repository;

import com.girigiri.kwrental.item.domain.Item;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ItemJpaRepository extends Repository<Item, Long>, ItemQueryDslRepositoryCustom, ItemJdbcRepositoryCustom {
    Optional<Item> findById(Long id);

    Item save(Item item);

    Optional<Item> findByPropertyNumber(String propertyNumber);
}