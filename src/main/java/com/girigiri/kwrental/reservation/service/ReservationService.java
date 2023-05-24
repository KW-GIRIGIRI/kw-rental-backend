package com.girigiri.kwrental.reservation.service;

import com.girigiri.kwrental.asset.domain.Rentable;
import com.girigiri.kwrental.asset.service.AssetService;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.inventory.service.InventoryService;
import com.girigiri.kwrental.reservation.domain.*;
import com.girigiri.kwrental.reservation.dto.request.AddLabRoomReservationRequest;
import com.girigiri.kwrental.reservation.dto.request.AddReservationRequest;
import com.girigiri.kwrental.reservation.dto.request.RentLabRoomRequest;
import com.girigiri.kwrental.reservation.dto.request.ReturnLabRoomRequest;
import com.girigiri.kwrental.reservation.dto.response.LabRoomReservationWithMemberNumberResponse;
import com.girigiri.kwrental.reservation.dto.response.ReservationsByEquipmentPerYearMonthResponse;
import com.girigiri.kwrental.reservation.dto.response.UnterminatedReservationsResponse;
import com.girigiri.kwrental.reservation.exception.*;
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
        inventoryService.deleteAll(memberId);
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
    public Set<EquipmentReservationWithMemberNumber> getReservationsByStartDate(final LocalDate startDate) {
        return reservationSpecRepository.findEquipmentReservationWhenAccept(startDate);
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
                .filter(ReservationSpec::isReserved)
                .map(ReservationSpec::getId)
                .collect(Collectors.toSet());
        if (reservationSpecIdsFromInput.containsAll(reservationSpecIdsFromReservation) &&
                reservationSpecIdsFromReservation.containsAll(reservationSpecIdsFromInput)) return;
        throw new ReservationSpecException("신청한 대여 상세와 입력된 대여 상세가 일치하지 않습니다.");
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public List<Reservation> rentLabRoom(final RentLabRoomRequest rentLabRoomRequest) {
        final List<Reservation> reservations = reservationRepository.findByReservationSpecIds(rentLabRoomRequest.getReservationSpecIds());
        validateSameLabRoom(rentLabRoomRequest.getName(), reservations);
        for (Reservation reservation : reservations) {
            final List<ReservationSpec> specs = reservation.getReservationSpecs();
            if (specs.size() != 1) throw new LabRoomReservationSpecNotOneException();
            if (!specs.stream().allMatch(ReservationSpec::isReserved))
                throw new LabRoomReservationNotReservedWhenAcceptException();
            if (!specs.stream().allMatch(spec -> spec.containsDate(LocalDate.now())))
                throw new IllegalRentDateException();
            final List<Long> specIds = reservation
                    .getReservationSpecs()
                    .stream().map(ReservationSpec::getId).toList();
            acceptReservation(reservation.getId(), specIds);
        }
        return reservations;
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Set<EquipmentReservationWithMemberNumber> getOverdueReservationsWithMemberNumber(final LocalDate localDate) {
        return reservationSpecRepository.findOverdueEquipmentReservationWhenReturn(localDate);
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Set<EquipmentReservationWithMemberNumber> getReservationsWithMemberNumberByEndDate(final LocalDate localDate) {
        return reservationSpecRepository.findEquipmentReservationWhenReturn(localDate);
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Reservation getReservationWithReservationSpecsById(final Long id) {
        return reservationRepository.findByIdWithSpecs(id)
                .orElseThrow(ReservationNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public UnterminatedReservationsResponse getUnterminatedReservations(final Long memberId) {
        final Set<Reservation> reservations = reservationRepository.findNotTerminatedEquipmentReservationsByMemberId(memberId);
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

    @Transactional(readOnly = true)
    public Set<LabRoomReservationWithMemberNumberResponse> getLabRoomReservationForAccept(final LocalDate date) {
        return reservationSpecRepository.findLabRoomReservationsWhenAccept(date);
    }

    @Transactional(readOnly = true)
    public Set<LabRoomReservationWithMemberNumberResponse> getLabRoomReservationForReturn(final LocalDate date) {
        return reservationSpecRepository.findLabRoomReservationWhenReturn(date);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void acceptReservation(final Long id, final List<Long> rentedReservationSpecIds) {
        final Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(ReservationNotFoundException::new);
        reservation.acceptAt(LocalDateTime.now());
        reservationSpecRepository.updateStatusByIds(rentedReservationSpecIds, ReservationSpecStatus.RENTED);
    }

    private void validateSameLabRoom(final String labRoomName, final List<Reservation> reservations) {
        final boolean isSameRentable = reservations.stream()
                .allMatch(reservation -> reservation.isOnlyRentFor(labRoomName));
        if (!isSameRentable) throw new NotSameRentableRentException();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public List<Reservation> returnLabRoom(final ReturnLabRoomRequest returnLabRoomRequest) {
        final List<Reservation> reservations = reservationRepository.findByReservationSpecIds(returnLabRoomRequest.getReservationSpecIds());
        validateSameLabRoom(returnLabRoomRequest.getName(), reservations);
        for (Reservation reservation : reservations) {
            final List<ReservationSpec> specs = reservation.getReservationSpecs();
            if (specs.size() != 1) throw new LabRoomReservationSpecNotOneException();
            if (!specs.stream().allMatch(ReservationSpec::isRented))
                throw new LabRoomReservationNotRentedWhenReturnException();
            specs.forEach(spec -> spec.setStatus(ReservationSpecStatus.RETURNED));
            reservation.updateIfTerminated();
        }
        return reservations;
    }
}
