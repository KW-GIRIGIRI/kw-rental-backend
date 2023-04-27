package com.girigiri.kwrental.reservation.service;

import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.service.InventoryService;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationCalendar;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.dto.request.AddReservationRequest;
import com.girigiri.kwrental.reservation.dto.response.ReservationsByEquipmentPerYearMonthResponse;
import com.girigiri.kwrental.reservation.dto.response.ReservationsByStartDateResponse;
import com.girigiri.kwrental.reservation.exception.ReservationNotFoundException;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepositoryCustom;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final InventoryService inventoryService;
    private final RemainingQuantityServiceImpl remainingQuantityService;
    private final ReservationSpecRepositoryCustom rentalSpecRepository;

    public ReservationService(final ReservationRepository reservationRepository, final InventoryService inventoryService,
                              final RemainingQuantityServiceImpl remainingQuantityService,
                              final ReservationSpecRepository rentalSpecRepository) {
        this.reservationRepository = reservationRepository;
        this.inventoryService = inventoryService;
        this.remainingQuantityService = remainingQuantityService;
        this.rentalSpecRepository = rentalSpecRepository;
    }

    @Transactional
    public Long reserve(final AddReservationRequest addReservationRequest) {
        final List<Inventory> inventories = inventoryService.getInventoriesWithEquipment();
        final List<ReservationSpec> reservationSpecs = inventories.stream()
                .filter(this::isAvailableCountValid)
                .map(this::mapToRentalSpec)
                .toList();
        final Reservation reservation = mapToReservation(addReservationRequest, reservationSpecs);
        return reservationRepository.save(reservation).getId();
    }

    private boolean isAvailableCountValid(final Inventory inventory) {
        remainingQuantityService.validateAmount(inventory.getEquipment().getId(), inventory.getRentalAmount().getAmount(), inventory.getRentalPeriod());
        return true;
    }

    private ReservationSpec mapToRentalSpec(final Inventory inventory) {
        return ReservationSpec.builder().period(inventory.getRentalPeriod())
                .amount(inventory.getRentalAmount())
                .equipment(inventory.getEquipment())
                .build();
    }

    private Reservation mapToReservation(final AddReservationRequest addReservationRequest, final List<ReservationSpec> reservationSpecs) {
        return Reservation.builder()
                .reservationSpecs(reservationSpecs)
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
        final List<ReservationSpec> reservationSpecs = rentalSpecRepository.findByStartDateBetween(equipmentId, startOfMonth, endOfMonth);
        final ReservationCalendar calendar = ReservationCalendar.from(startOfMonth, endOfMonth);
        calendar.addAll(reservationSpecs);
        return ReservationsByEquipmentPerYearMonthResponse.from(calendar);
    }

    @Transactional(readOnly = true)
    public ReservationsByStartDateResponse getReservationsByStartDate(final LocalDate startDate) {
        final List<Reservation> reservations = reservationRepository.findReservationsWithSpecsByStartDate(startDate);
        return ReservationsByStartDateResponse.from(reservations);
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Reservation getReservationByIdWithSpecs(final Long id) {
        return reservationRepository.findByIdWithSpecs(id)
                .orElseThrow(ReservationNotFoundException::new);
    }
}
