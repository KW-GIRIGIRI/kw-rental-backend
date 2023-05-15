package com.girigiri.kwrental.rental.repository;

import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.rental.repository.dto.RentalDto;
import com.girigiri.kwrental.rental.repository.dto.RentalSpecStatuesPerPropertyNumber;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface RentalSpecRepositoryCustom {
    List<RentalSpec> findByPropertyNumbers(Set<String> propertyNumbers);

    List<RentalSpec> findByReservationSpecIds(Set<Long> reservationSpecIds);

    Set<RentalSpec> findRentedRentalSpecs(Long equipmentId, LocalDateTime date);

    List<RentalSpec> findByReservationId(Long reservationId);

    List<RentalDto> findRentalDtosBetweenDate(Long memberId, LocalDate from, LocalDate to);

    List<RentalSpecStatuesPerPropertyNumber> findStatusesByPropertyNumbersBetweenDate(Set<String> propertyNumbers, LocalDate from, LocalDate to);
}
