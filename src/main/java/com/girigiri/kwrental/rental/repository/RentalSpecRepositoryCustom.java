package com.girigiri.kwrental.rental.repository;

import com.girigiri.kwrental.rental.domain.RentalSpec;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface RentalSpecRepositoryCustom {
    List<RentalSpec> findByPropertyNumbers(Set<String> propertyNumbers);

    List<RentalSpec> findByReservationSpecIds(Set<Long> reservationSpecIds);

    Set<RentalSpec> findRentedRentalSpecs(Long equipmentId, LocalDateTime date);
}
