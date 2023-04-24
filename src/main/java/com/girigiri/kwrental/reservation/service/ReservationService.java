package com.girigiri.kwrental.reservation.service;

import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.service.InventoryService;
import com.girigiri.kwrental.reservation.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationCalendar;
import com.girigiri.kwrental.reservation.dto.request.AddReservationRequest;
import com.girigiri.kwrental.reservation.dto.response.ReservationsByEquipmentPerYearMonthResponse;
import com.girigiri.kwrental.reservation.repository.RentalSpecRepositoryCustom;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final InventoryService inventoryService;
    private final RemainingQuantityServiceImpl remainingQuantityService;
    private final RentalSpecRepositoryCustom rentalSpecRepository;

    public ReservationService(final ReservationRepository reservationRepository, final InventoryService inventoryService,
                              final RemainingQuantityServiceImpl remainingQuantityService,
                              @Qualifier("rentalSpecRepository") final RentalSpecRepositoryCustom rentalSpecRepository) {
        this.reservationRepository = reservationRepository;
        this.inventoryService = inventoryService;
        this.remainingQuantityService = remainingQuantityService;
        this.rentalSpecRepository = rentalSpecRepository;
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
        remainingQuantityService.validateAmount(inventory.getEquipment().getId(), inventory.getRentalAmount().getAmount(), inventory.getRentalPeriod());
        return true;
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

    @Transactional(readOnly = true)
    public ReservationsByEquipmentPerYearMonthResponse getReservationsByEquipmentsPerYearMonth(final Long equipmentId, final YearMonth yearMonth) {
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();
        final List<RentalSpec> rentalSpecs = rentalSpecRepository.findByStartDateBetween(equipmentId, startOfMonth, endOfMonth);
        final ReservationCalendar calendar = ReservationCalendar.from(startOfMonth, endOfMonth);
        calendar.addAll(rentalSpecs);
        return ReservationsByEquipmentPerYearMonthResponse.from(calendar);
    }
}
