package com.girigiri.kwrental.reservation.service;

import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.service.InventoryService;
import com.girigiri.kwrental.item.service.ItemServiceImpl;
import com.girigiri.kwrental.reservation.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.dto.request.AddReservationRequest;
import com.girigiri.kwrental.reservation.repository.RentalSpecRepository;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RentalSpecRepository rentalSpecRepository;
    private final InventoryService inventoryService;
    private final ItemServiceImpl itemService;

    public ReservationService(final ReservationRepository reservationRepository, final RentalSpecRepository rentalSpecRepository,
                              final InventoryService inventoryService, final ItemServiceImpl itemService) {
        this.reservationRepository = reservationRepository;
        this.rentalSpecRepository = rentalSpecRepository;
        this.inventoryService = inventoryService;
        this.itemService = itemService;
    }

    @Transactional
    public Long reserve(final AddReservationRequest addReservationRequest) {
        final List<Inventory> inventories = inventoryService.getInventoriesWithEquipment();
        final List<RentalSpec> rentalSpecs = inventories.stream()
                .filter(this::isAvailableCountValid)
                .map(this::mapToRentalSpec)
                .toList();
        final Reservation reservation = mapToReservation(addReservationRequest, rentalSpecs);
        return reservationRepository.save(reservation).getId();
    }

    private boolean isAvailableCountValid(final Inventory inventory) {
        final int rentedCount = getRentedCount(inventory);
        itemService.validateAvailableCount(inventory.getEquipment().getId(), rentedCount + inventory.getRentalAmount().getAmount());
        return true;
    }

    private int getRentedCount(final Inventory inventory) {
        final List<RentalSpec> overlappedRentalSpec = rentalSpecRepository.findOverlappedByPeriod(inventory.getEquipment().getId(), inventory.getRentalPeriod());
        return overlappedRentalSpec.stream()
                .mapToInt(it -> it.getAmount().getAmount())
                .sum();
    }

    private RentalSpec mapToRentalSpec(final Inventory inventory) {
        return RentalSpec.builder().period(inventory.getRentalPeriod())
                .amount(inventory.getRentalAmount())
                .equipment(inventory.getEquipment())
                .build();
    }

    private Reservation mapToReservation(final AddReservationRequest addReservationRequest, final List<RentalSpec> rentalSpecs) {
        return Reservation.builder()
                .rentalSpecs(rentalSpecs)
                .email(addReservationRequest.getRenterEmail())
                .name(addReservationRequest.getRenterName())
                .purpose(addReservationRequest.getRentalPurpose())
                .phoneNumber(addReservationRequest.getRenterPhoneNumber())
                .build();
    }
}
