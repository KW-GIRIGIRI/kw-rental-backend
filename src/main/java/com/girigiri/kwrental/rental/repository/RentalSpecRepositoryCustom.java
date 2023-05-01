package com.girigiri.kwrental.rental.repository;

import com.girigiri.kwrental.rental.domain.RentalSpec;

import java.util.List;
import java.util.Set;

public interface RentalSpecRepositoryCustom {
    List<RentalSpec> findByPropertyNumbers(Set<String> propertyNumbers);

    List<RentalSpec> findByReservationId(Set<Long> reservationSpecIds);
}
