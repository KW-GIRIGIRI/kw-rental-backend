package com.girigiri.kwrental.reservation.service;

import com.girigiri.kwrental.asset.domain.Rentable;
import com.girigiri.kwrental.asset.service.AssetService;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.inventory.service.InventoryService;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationCalendar;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.ReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.dto.request.AddLabRoomReservationRequest;
import com.girigiri.kwrental.reservation.dto.request.AddReservationRequest;
import com.girigiri.kwrental.reservation.dto.response.ReservationsByEquipmentPerYearMonthResponse;
import com.girigiri.kwrental.reservation.dto.response.UnterminatedReservationsResponse;
import com.girigiri.kwrental.reservation.exception.ReservationNotFoundException;
import com.girigiri.kwrental.reservation.exception.ReservationSpecException;
import com.girigiri.kwrental.reservation.exception.ReservationSpecNotFoundException;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final InventoryService inventoryService;
    private final RemainingQuantityServiceImpl remainingQuantityService;
    private final ReservationSpecRepository reservationSpecRepository;

    private final AssetService assetService;

    public ReservationService(final ReservationRepository reservationRepository, final InventoryService inventoryService,
                              final RemainingQuantityServiceImpl remainingQuantityService,
                              final ReservationSpecRepository reservationSpecRepository, final AssetService assetService) {
        this.reservationRepository = reservationRepository;
        this.inventoryService = inventoryService;
        this.remainingQuantityService = remainingQuantityService;
        this.reservationSpecRepository = reservationSpecRepository;
        this.assetService = assetService;
    }

    @Transactional
    public Long reserve(final Long memberId, final AddReservationRequest addReservationRequest) {
        final List<Inventory> inventories = inventoryService.getInventoriesWithEquipment(memberId);
        final List<ReservationSpec> reservationSpecs = inventories.stream()
                .filter(this::isAvailableCountValid)
                .map(this::mapToReservationSpec)
                .toList();
        final Reservation reservation = mapToReservation(memberId, addReservationRequest, reservationSpecs);
        return reservationRepository.save(reservation).getId();
    }

    private boolean isAvailableCountValid(final Inventory inventory) {
        remainingQuantityService.validateAmount(inventory.getRentable().getId(), inventory.getRentalAmount().getAmount(), inventory.getRentalPeriod());
        return true;
    }

    private ReservationSpec mapToReservationSpec(final Inventory inventory) {
        return ReservationSpec.builder().period(inventory.getRentalPeriod())
                .amount(inventory.getRentalAmount())
                .rentable(inventory.getRentable())
                .build();
    }

    private Reservation mapToReservation(final Long memberId, final AddReservationRequest addReservationRequest, final List<ReservationSpec> reservationSpecs) {
        return Reservation.builder()
                .reservationSpecs(reservationSpecs)
                .email(addReservationRequest.getRenterEmail())
                .name(addReservationRequest.getRenterName())
                .purpose(addReservationRequest.getRentalPurpose())
                .phoneNumber(addReservationRequest.getRenterPhoneNumber())
                .memberId(memberId)
                .build();
    }

    @Transactional
    public Long reserve(final Long memberId, final AddLabRoomReservationRequest addLabRoomReservationRequest) {
        final Rentable rentable = assetService.getRentableByName(addLabRoomReservationRequest.getLabRoomName());
        final RentalPeriod period = new RentalPeriod(addLabRoomReservationRequest.getStartDate(), addLabRoomReservationRequest.getEndDate());
        final RentalAmount amount = RentalAmount.ofPositive(addLabRoomReservationRequest.getRenterCount());
        remainingQuantityService.validateAmount(rentable.getId(), amount.getAmount(), period);
        final ReservationSpec spec = mapToReservationSpec(rentable, period, amount);
        final Reservation reservation = mapToReservation(memberId, addLabRoomReservationRequest, spec);
        reservationRepository.save(reservation);
        return reservation.getId();
    }

    private ReservationSpec mapToReservationSpec(final Rentable rentable, final RentalPeriod period, final RentalAmount amount) {
        return ReservationSpec.builder()
                .period(period)
                .amount(amount)
                .rentable(rentable)
                .build();
    }

    private Reservation mapToReservation(final Long memberId, final AddLabRoomReservationRequest addLabRoomReservationRequest, final ReservationSpec spec) {
        return Reservation.builder()
                .reservationSpecs(List.of(spec))
                .memberId(memberId)
                .email(addLabRoomReservationRequest.getRenterEmail())
                .name(addLabRoomReservationRequest.getRenterName())
                .purpose(addLabRoomReservationRequest.getRentalPurpose())
                .phoneNumber(addLabRoomReservationRequest.getRenterPhoneNumber())
                .build();
    }

    @Transactional(readOnly = true)
    public ReservationsByEquipmentPerYearMonthResponse getReservationsByEquipmentsPerYearMonth(final Long equipmentId, final YearMonth yearMonth) {
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();
        final List<ReservationSpec> reservationSpecs = reservationSpecRepository.findByStartDateBetween(equipmentId, startOfMonth, endOfMonth);
        final ReservationCalendar calendar = ReservationCalendar.from(startOfMonth, endOfMonth);
        calendar.addAll(reservationSpecs);
        return ReservationsByEquipmentPerYearMonthResponse.from(calendar);
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Set<ReservationWithMemberNumber> getReservationsByStartDate(final LocalDate startDate) {
        return reservationRepository.findUnterminatedReservationsWithSpecsByStartDate(startDate);
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Map<Long, Set<String>> validatePropertyNumbersCountAndGroupByEquipmentId(final Long reservationId, final Map<Long, Set<String>> propertyNumbersByReservationSpecId) {
        final Reservation reservation = reservationRepository.findByIdWithSpecs(reservationId)
                .orElseThrow(ReservationNotFoundException::new);
        validateReservationSpecIdContainsAll(reservation, propertyNumbersByReservationSpecId.keySet());
        Map<Long, Set<String>> collectedByEquipmentId = new HashMap<>();
        for (ReservationSpec reservationSpec : reservation.getReservationSpecs()) {
            reservationSpec.validateAmount(propertyNumbersByReservationSpecId.get(reservationSpec.getId()).size());
            collectedByEquipmentId.put(reservationSpec.getRentable().getId(), propertyNumbersByReservationSpecId.get(reservationSpec.getId()));
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

    @Transactional(propagation = Propagation.MANDATORY)
    public void acceptReservation(final Long id) {
        reservationRepository.findById(id)
                .orElseThrow(ReservationNotFoundException::new)
                .acceptAt(LocalDateTime.now());
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Set<ReservationWithMemberNumber> getOverdueReservationsWithMemberNumber(final LocalDate localDate) {
        return reservationRepository.findUnterminatedOverdueReservationWithSpecs(localDate);
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Set<ReservationWithMemberNumber> getReservationsWithMemberNumberByEndDate(final LocalDate localDate) {
        return reservationRepository.findUnterminatedReservationsWithSpecsByEndDate(localDate);
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Reservation getReservationWithReservationSpecsById(final Long id) {
        return reservationRepository.findByIdWithSpecs(id)
                .orElseThrow(ReservationNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public UnterminatedReservationsResponse getUnterminatedReservations(final Long memberId) {
        final Set<Reservation> reservations = reservationRepository.findNotTerminatedReservationsByMemberId(memberId);
        final List<Reservation> reservationList = new ArrayList<>(reservations);
        reservationList.sort(Comparator.comparing(Reservation::getRentalPeriod));
        return UnterminatedReservationsResponse.from(reservationList);
    }

    @Transactional
    public Long cancelReservationSpec(final Long reservationSpecId, final Integer amount) {
        final ReservationSpec reservationSpec = getReservationSpec(reservationSpecId);
        reservationSpec.cancelAmount(amount);
        reservationSpecRepository.adjustAmountAndStatus(reservationSpec);
        final Long reservationId = reservationSpec.getReservation().getId();
        updateAndAdjustTerminated(reservationId);
        return reservationSpec.getId();
    }

    private ReservationSpec getReservationSpec(final Long reservationSpecId) {
        return reservationSpecRepository.findById(reservationSpecId)
                .orElseThrow(ReservationSpecNotFoundException::new);
    }

    private void updateAndAdjustTerminated(final Long reservationId) {
        final Reservation reservation = reservationRepository.findByIdWithSpecs(reservationId)
                .orElseThrow(ReservationNotFoundException::new);
        reservation.updateIfTerminated();
        if (reservation.isTerminated()) reservationRepository.adjustTerminated(reservation);
    }
}
