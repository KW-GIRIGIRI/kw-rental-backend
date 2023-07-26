package com.girigiri.kwrental.rental.service.rent.validator;

import static java.util.stream.Collectors.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.girigiri.kwrental.item.service.ItemService;
import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.dto.request.CreateEquipmentRentalRequest;
import com.girigiri.kwrental.rental.dto.request.CreateEquipmentRentalRequest.EquipmentRentalSpecsRequest;
import com.girigiri.kwrental.rental.exception.DuplicateRentalException;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.reservation.service.ReservationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EquipmentRentValidator implements RentValidator<CreateEquipmentRentalRequest> {

	private final ReservationService reservationService;
	private final ItemService itemService;
	private final RentalSpecRepository rentalSpecRepository;

	@Override
	public void validate(final CreateEquipmentRentalRequest rentalRequest) {
		validateEquipmentRentalAmount(rentalRequest);
		validatePropertyNumbersAndAlreadyRented(rentalRequest);
	}

	private void validateEquipmentRentalAmount(final CreateEquipmentRentalRequest createEquipmentRentalRequest) {
		final Map<Long, Integer> amountPerReservationSpecId = groupAmountByReservationSpecId(
			createEquipmentRentalRequest);
		reservationService.validateReservationSpecHasSameAmount(amountPerReservationSpecId);
	}

	private Map<Long, Integer> groupAmountByReservationSpecId(
		final CreateEquipmentRentalRequest createEquipmentRentalRequest) {
		return createEquipmentRentalRequest.equipmentRentalSpecsRequests()
			.stream()
			.collect(toMap(EquipmentRentalSpecsRequest::reservationSpecId, it -> it.propertyNumbers().size()));
	}

	private void validatePropertyNumbersAndAlreadyRented(
		final CreateEquipmentRentalRequest createEquipmentRentalRequest) {
		Map<Long, Set<String>> propertyNumbersByEquipmentId = groupPropertyNumbersByEquipmentId(
			createEquipmentRentalRequest);
		itemService.validatePropertyNumbers(propertyNumbersByEquipmentId);
		validateAlreadyRented(collectPropertyNumbers(propertyNumbersByEquipmentId));
	}

	private Map<Long, Set<String>> groupPropertyNumbersByEquipmentId(
		final CreateEquipmentRentalRequest createEquipmentRentalRequest) {
		final Map<Long, Set<String>> propertyNumbersByReservationSpecId = groupPropertyNumberByReservationSpecId(
			createEquipmentRentalRequest);
		final Long reservationId = createEquipmentRentalRequest.reservationId();
		return reservationService.groupPropertyNumbersByEquipmentId(reservationId, propertyNumbersByReservationSpecId);
	}

	private Map<Long, Set<String>> groupPropertyNumberByReservationSpecId(
		final CreateEquipmentRentalRequest createEquipmentRentalRequest) {
		return createEquipmentRentalRequest.equipmentRentalSpecsRequests()
			.stream()
			.collect(toMap(EquipmentRentalSpecsRequest::reservationSpecId, it -> Set.copyOf(it.propertyNumbers())));
	}

	private void validateAlreadyRented(final Set<String> propertyNumbers) {
		final boolean nowRental = rentalSpecRepository.findByPropertyNumbers(propertyNumbers)
			.stream()
			.anyMatch(EquipmentRentalSpec::isNowRental);
		if (nowRental)
			throw new DuplicateRentalException();
	}

	private Set<String> collectPropertyNumbers(final Map<Long, Set<String>> collectedByEquipmentId) {
		return collectedByEquipmentId.values().stream()
			.flatMap(Set::stream)
			.collect(Collectors.toSet());
	}
}
