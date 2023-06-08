package com.girigiri.kwrental.item.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.girigiri.kwrental.item.domain.Item;

public interface ItemRepository extends Repository<Item, Long>, ItemQueryDslRepositoryCustom, ItemJdbcRepositoryCustom {
    List<Item> findByAssetId(Long assetId);

    Optional<Item> findById(Long id);

    Item save(Item item);

    Optional<Item> findByPropertyNumber(String propertyNumber);
}
