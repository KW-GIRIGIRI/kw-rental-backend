package com.girigiri.kwrental.rental.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.girigiri.kwrental.rental.domain.entity.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.domain.entity.RentalSpec;
import com.girigiri.kwrental.rental.dto.response.EquipmentRentalsDto.EquipmentRentalDto;
import com.girigiri.kwrental.rental.dto.response.LabRoomRentalsDto.LabRoomRentalDto;
import com.girigiri.kwrental.rental.dto.response.LabRoomReservationResponse;
import com.girigiri.kwrental.rental.dto.response.RentalSpecStatuesPerPropertyNumber;
import com.girigiri.kwrental.rental.dto.response.RentalSpecWithName;
import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;

public interface RentalSpecRepositoryCustom {
	List<EquipmentRentalSpec> findByPropertyNumbers(Set<String> propertyNumbers);

	List<RentalSpec> findByReservationSpecIds(Set<Long> reservationSpecIds);

	Set<EquipmentRentalSpec> findRentedRentalSpecsByAssetId(Long equipmentId, LocalDateTime date);

	List<EquipmentRentalSpec> findByReservationId(Long reservationId);

	List<EquipmentRentalDto> findEquipmentRentalDtosBetweenDate(Long memberId, LocalDate from, LocalDate to);

	List<LabRoomRentalDto> findLabRoomRentalDtosBetweenDate(Long memberId, LocalDate from, LocalDate to);

	List<RentalSpecStatuesPerPropertyNumber> findStatusesByPropertyNumbersBetweenDate(Set<String> propertyNumbers,
		LocalDate from, LocalDate to);

	List<RentalSpecWithName> findTerminatedWithNameByPropertyNumber(String propertyNumber);

	List<LabRoomReservationResponse> getReturnedLabRoomReservationResponse(String labRoomName, LocalDate date);

	Page<LabRoomReservationResponse> getReturnedLabRoomReservationResponse(String labRoomName,
		LocalDate startDate, LocalDate endDate, Pageable pageable);

	void updatePropertyNumber(String from, String to);

	List<RentalSpec> findRentedRentalSpecsByAssetId(Long assetId);

	List<EquipmentRentalSpec> findRentedRentalSpecsByPropertyNumber(String propertyNumber);

	List<RentalSpecWithName> findTerminatedWithNameByPropertyAndInclusive(final String propertyNumber,
		final RentalDateTime startDate, final RentalDateTime endDate);
}
