package com.girigiri.kwrental.item.repository;

public interface ItemQueryDslRepositoryCustom {

    int updateRentalAvailable(Long id, boolean rentalAvailable);

    int updatePropertyNumber(Long id, String propertyNumber);
}
