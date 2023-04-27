package com.girigiri.kwrental.rental.service;

import com.girigiri.kwrental.item.service.ItemServiceImpl;
import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.rental.dto.request.CreateRentalRequest;
import com.girigiri.kwrental.rental.dto.request.RentalSpecsRequest;
import com.girigiri.kwrental.rental.exception.DuplicateRentalException;
import com.girigiri.kwrental.rental.repository.RentalSpecRepository;
import com.girigiri.kwrental.reservation.service.ReservationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Service
public class RentalService {

    private final ItemServiceImpl itemService;
    private final ReservationService reservationService;
    private final RentalSpecRepository rentalSpecRepository;

    public RentalService(final ItemServiceImpl itemService, final ReservationService reservationService, final RentalSpecRepository rentalSpecRepository) {
        this.itemService = itemService;
        this.reservationService = reservationService;
        this.rentalSpecRepository = rentalSpecRepository;
    }

    @Transactional
    public void rent(final CreateRentalRequest createRentalRequest) {
        final Map<Long, Set<String>> propertyNumbersByReservationSpecId = createRentalRequest.getRentalSpecsRequests().stream()
                .collect(toMap(RentalSpecsRequest::getReservationSpecId, it -> Set.copyOf(it.getPropertyNumbers())));
        Map<Long, Set<String>> collectedByEquipmentId = reservationService.validatePropertyNumbersCountAndGroupByEquipmentId(createRentalRequest.getReservationId(), propertyNumbersByReservationSpecId);
        itemService.validatePropertyNumbers(collectedByEquipmentId);
        validateNowRental(collectedByEquipmentId);
        final List<RentalSpec> rentalSpecs = mapToRentalSpecs(createRentalRequest);
        rentalSpecRepository.saveAll(rentalSpecs);
    }

    private void validateNowRental(final Map<Long, Set<String>> collectedByEquipmentId) {
        final Set<String> propertyNumbers = collectedByEquipmentId.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
        final boolean nowRental = rentalSpecRepository.findByPropertyNumbers(propertyNumbers).stream()
                .anyMatch(RentalSpec::isNowRental);
        if (nowRental) throw new DuplicateRentalException();
    }

    private List<RentalSpec> mapToRentalSpecs(final CreateRentalRequest createRentalRequest) {
        return createRentalRequest.getRentalSpecsRequests().stream()
                .map(RentalSpecsRequest::getPropertyNumbers)
                .flatMap(List::stream)
                .map(propertyNumber -> mapToRentalSpec(createRentalRequest.getReservationId(), propertyNumber))
                .toList();
    }

    private RentalSpec mapToRentalSpec(final Long reservationSpecId, final String propertyNumber) {
        return RentalSpec.builder()
                .propertyNumber(propertyNumber)
                .reservationSpecId(reservationSpecId)
                .build();
    }
}
