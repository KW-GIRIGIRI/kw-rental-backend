package com.girigiri.kwrental.item.service;

import java.time.LocalDateTime;
import java.util.Set;

public interface RentedItemService {

    Set<String> getRentedPropertyNumbers(Long equipmentId, LocalDateTime date);
}
