package com.girigiri.kwrental.item.repository;

import com.girigiri.kwrental.item.domain.Item;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends Repository<Item, Long>, ItemQueryDslRepositoryCustom, ItemJdbcRepositoryCustom {
    void deleteByAssetId(Long assetId);

    List<Item> findByAssetId(Long assetId);

    Optional<Item> findById(Long id);

    Item save(Item item);

    void deleteById(Long id);

    Optional<Item> findByPropertyNumber(String propertyNumber);
}
