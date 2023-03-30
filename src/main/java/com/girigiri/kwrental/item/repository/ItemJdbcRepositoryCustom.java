package com.girigiri.kwrental.item.repository;

import com.girigiri.kwrental.item.domain.Item;
import java.util.List;

public interface ItemJdbcRepositoryCustom {

    int saveAll(List<Item> items);
}
