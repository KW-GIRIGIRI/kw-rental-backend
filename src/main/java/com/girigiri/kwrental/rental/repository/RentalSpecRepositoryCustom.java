package com.girigiri.kwrental.rental.repository;

import com.girigiri.kwrental.inventory.domain.RentalDateTime;
import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.dto.response.RentalSpecWithName;
import com.girigiri.kwrental.rental.repository.dto.RentalDto;
import com.girigiri.kwrental.rental.repository.dto.RentalSpecStatuesPerPropertyNumber;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface RentalSpecRepositoryCustom {
    List<EquipmentRentalSpec> findByPropertyNumbers(Set<String> propertyNumbers);

    List<EquipmentRentalSpec> findByReservationSpecIds(Set<Long> reservationSpecIds);

    Set<EquipmentRentalSpec> findRentedRentalSpecs(Long equipmentId, LocalDateTime date);

    List<EquipmentRentalSpec> findByReservationId(Long reservationId);

    List<RentalDto> findRentalDtosBetweenDate(Long memberId, LocalDate from, LocalDate to);

    List<RentalSpecStatuesPerPropertyNumber> findStatusesByPropertyNumbersBetweenDate(Set<String> propertyNumbers, LocalDate from, LocalDate to);

    List<RentalSpecWithName> findTerminatedWithNameByPropertyNumber(String propertyNumber);

    void updateNormalReturnedByReservationIds(List<Long> reservationIds, RentalDateTime returnDateTime);
}
