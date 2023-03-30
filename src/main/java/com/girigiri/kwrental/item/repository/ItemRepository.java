package com.girigiri.kwrental.item.repository;

import com.girigiri.kwrental.item.domain.Item;
import java.util.List;
import org.springframework.data.repository.Repository;
import org.springframework.lang.NonNull;

public interface ItemRepository extends Repository<Item, Long>, ItemJdbcRepositoryCustom {
    List<Item> findByEquipmentId(@NonNull Long equipmentId);
}
