package com.girigiri.kwrental.reservation.service;

import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.service.InventoryService;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationCalendar;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.dto.request.AddReservationRequest;
import com.girigiri.kwrental.reservation.dto.response.ReservationsByEquipmentPerYearMonthResponse;
import com.girigiri.kwrental.reservation.exception.ReservationNotFoundException;
import com.girigiri.kwrental.reservation.exception.ReservationSpecException;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepositoryCustom;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public List<Reservation> getReservationsByStartDate(final LocalDate startDate) {
        return reservationRepository.findReservationsWithSpecsByStartDate(startDate);
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Map<Long, Set<String>> validatePropertyNumbersCountAndGroupByEquipmentId(final Long reservationId, final Map<Long, Set<String>> propertyNumbersByReservationSpecId) {
        final Reservation reservation = reservationRepository.findByIdWithSpecs(reservationId)
                .orElseThrow(ReservationNotFoundException::new);
        validateReservationSpecIdContainsAll(reservation, propertyNumbersByReservationSpecId.keySet());
        Map<Long, Set<String>> collectedByEquipmentId = new HashMap<>();
        for (ReservationSpec reservationSpec : reservation.getReservationSpecs()) {
            reservationSpec.validateAmount(propertyNumbersByReservationSpecId.get(reservationSpec.getId()).size());
            collectedByEquipmentId.put(reservationSpec.getEquipment().getId(), propertyNumbersByReservationSpecId.get(reservationSpec.getId()));
        }
        return collectedByEquipmentId;
    }

    private void validateReservationSpecIdContainsAll(final Reservation reservation, final Set<Long> reservationSpecIdsFromInput) {
        final Set<Long> reservationSpecIdsFromReservation = reservation.getReservationSpecs().stream()
                .map(ReservationSpec::getId)
                .collect(Collectors.toSet());
        if (reservationSpecIdsFromInput.containsAll(reservationSpecIdsFromReservation) &&
                reservationSpecIdsFromReservation.containsAll(reservationSpecIdsFromInput)) return;
        throw new ReservationSpecException("입력된 대여 예약 상세가 맞지 않습니다.");
    }

    public void acceptReservation(final Long id) {
        reservationRepository.findById(id)
                .orElseThrow(ReservationNotFoundException::new)
                .acceptAt(LocalDateTime.now());
    }

    public List<Reservation> getOverdueReservations(final LocalDate localDate) {
        return reservationRepository.findOverdueReservationWithSpecs(localDate);
    }

    public List<Reservation> getReservationsByEndDate(final LocalDate localDate) {
        return reservationRepository.findReservationsWithSpecsByEndDate(localDate);
    }

    public Reservation getReservationWithReservationSpecsById(final Long id) {
        return reservationRepository.findByIdWithSpecs(id)
                .orElseThrow(ReservationNotFoundException::new);
    }
}
