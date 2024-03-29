package com.girigiri.kwrental.rental.service.rent.validator;

import static java.util.stream.Collectors.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.girigiri.kwrental.item.service.ItemValidator;
import com.girigiri.kwrental.rental.domain.entity.EquipmentRentalSpec;
import com.girigiri.kwrental.rental.dto.request.CreateEquipmentRentalRequest;
import com.girigiri.kwrental.rental.dto.request.CreateEquipmentRentalRequest.EquipmentRentalSpecsRequest;
import com.girigiri.kwrental.rental.exception.DuplicateRentalException;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.reservation.service.ReservationRetrieveService;
import com.girigiri.kwrental.reservation.service.ReservationValidator;

import lombok.RequiredArgsConstructor;

@Slf4j
@Component
@RequiredArgsConstructor
public class EquipmentRentValidator implements RentValidator<CreateEquipmentRentalRequest> {

    private final ReservationValidator reservationValidator;
    private final ReservationRetrieveService reservationRetrieveService;
    private final ItemValidator itemValidator;
    private final RentalSpecRepository rentalSpecRepository;

    @Override
    public void validate(final CreateEquipmentRentalRequest rentalRequest) {
        validateEquipmentRentalAmount(rentalRequest);
        validatePropertyNumbersAndAlreadyRented(rentalRequest);
    }

    private void validateEquipmentRentalAmount(final CreateEquipmentRentalRequest createEquipmentRentalRequest) {
        final Map<Long, Integer> amountPerReservationSpecId = groupAmountByReservationSpecId(
                createEquipmentRentalRequest);
        reservationValidator.validateAmountIsSame(amountPerReservationSpecId);
    }

    private Map<Long, Integer> groupAmountByReservationSpecId(
            final CreateEquipmentRentalRequest createEquipmentRentalRequest) {
        return createEquipmentRentalRequest.rentalSpecsRequests()
                .stream()
                .collect(toMap(EquipmentRentalSpecsRequest::reservationSpecId, it -> it.propertyNumbers().size()));
    }

    private void validatePropertyNumbersAndAlreadyRented(
            final CreateEquipmentRentalRequest createEquipmentRentalRequest) {
        Map<Long, Set<String>> propertyNumbersByEquipmentId = groupPropertyNumbersByEquipmentId(
                createEquipmentRentalRequest);
        propertyNumbersByEquipmentId.forEach((equipmentId, propertyNumbers) -> log.info(
                "equipmentId : {}, property numbers : {}", equipmentId, String.join(", ", propertyNumbers)));
        itemValidator.validatePropertyNumbers(propertyNumbersByEquipmentId);
        validateAlreadyRented(collectPropertyNumbers(propertyNumbersByEquipmentId));
    }

    private Map<Long, Set<String>> groupPropertyNumbersByEquipmentId(
            final CreateEquipmentRentalRequest createEquipmentRentalRequest) {
        final Map<Long, Set<String>> propertyNumbersByReservationSpecId = groupPropertyNumberByReservationSpecId(
                createEquipmentRentalRequest);
        propertyNumbersByReservationSpecId.forEach((key, value)-> log.info("[DEBUGGING] reservation spec id and property numbers : {}, {}", key, String.join(", ", value)));
        final Long reservationId = createEquipmentRentalRequest.reservationId();
        return reservationRetrieveService.groupPropertyNumbersByEquipmentId(reservationId,
                propertyNumbersByReservationSpecId);
    }

    private Map<Long, Set<String>> groupPropertyNumberByReservationSpecId(
            final CreateEquipmentRentalRequest createEquipmentRentalRequest) {
        return createEquipmentRentalRequest.rentalSpecsRequests()
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
