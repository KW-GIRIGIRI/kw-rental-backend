package com.girigiri.kwrental.item.repository;

import com.girigiri.kwrental.item.domain.Item;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface ItemRepository extends Repository<Item, Long>, ItemJdbcRepositoryCustom, ItemQueryDslRepositoryCustom {
    void deleteByEquipmentId(Long equipmentId);

    List<Item> findByEquipmentId(Long equipmentId);

    Optional<Item> findById(Long id);

    Item save(Item item);
}
