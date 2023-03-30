package com.girigiri.kwrental.item.repository;

import com.girigiri.kwrental.item.domain.Item;
import org.springframework.data.repository.Repository;

public interface ItemRepository extends Repository<Item, Long>, ItemJdbcRepositoryCustom {
}
