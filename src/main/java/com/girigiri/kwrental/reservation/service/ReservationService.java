package com.girigiri.kwrental.reservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.domain.Rentable;
import com.girigiri.kwrental.asset.service.AssetService;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.inventory.service.InventoryService;
import com.girigiri.kwrental.labroom.domain.LabRoom;
import com.girigiri.kwrental.labroom.exception.LabRoomNotAvailableException;
import com.girigiri.kwrental.labroom.service.LabRoomService;
import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.LabRoomReservation;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationCalendar;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.ReservationSpecStatus;
import com.girigiri.kwrental.reservation.dto.request.AddLabRoomReservationRequest;
import com.girigiri.kwrental.reservation.dto.request.AddReservationRequest;
import com.girigiri.kwrental.reservation.dto.request.RentLabRoomRequest;
import com.girigiri.kwrental.reservation.dto.request.ReturnLabRoomRequest;
import com.girigiri.kwrental.reservation.dto.response.HistoryStatResponse;
import com.girigiri.kwrental.reservation.dto.response.LabRoomReservationWithMemberNumberResponse;
import com.girigiri.kwrental.reservation.dto.response.RelatedReservationsInfoResponse;
import com.girigiri.kwrental.reservation.dto.response.ReservationsByEquipmentPerYearMonthResponse;
import com.girigiri.kwrental.reservation.dto.response.UnterminatedEquipmentReservationsResponse;
import com.girigiri.kwrental.reservation.dto.response.UnterminatedLabRoomReservationsResponse;
import com.girigiri.kwrental.reservation.exception.NotSameRentableRentException;
import com.girigiri.kwrental.reservation.exception.ReservationNotFoundException;
import com.girigiri.kwrental.reservation.exception.ReservationSpecException;
import com.girigiri.kwrental.reservation.exception.ReservationSpecNotFoundException;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;

@Service
public class ReservationService {

	private final ReservationRepository reservationRepository;
	private final InventoryService inventoryService;
	private final RemainingQuantityServiceImpl remainingQuantityService;
	private final ReservationSpecRepository reservationSpecRepository;
	private final AssetService assetService;
	private final LabRoomService labRoomService;

	public ReservationService(final ReservationRepository reservationRepository,
		final InventoryService inventoryService,
		final RemainingQuantityServiceImpl remainingQuantityService,
		final ReservationSpecRepository reservationSpecRepository, final AssetService assetService,
		LabRoomService labRoomService) {
		this.reservationRepository = reservationRepository;
		this.inventoryService = inventoryService;
		this.remainingQuantityService = remainingQuantityService;
		this.reservationSpecRepository = reservationSpecRepository;
		this.assetService = assetService;
		this.labRoomService = labRoomService;
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
		remainingQuantityService.validateAmount(inventory.getRentable().getId(),
			inventory.getRentalAmount().getAmount(), inventory.getRentalPeriod());
		return true;
	}

	private ReservationSpec mapToReservationSpec(final Inventory inventory) {
		return ReservationSpec.builder().period(inventory.getRentalPeriod())
			.amount(inventory.getRentalAmount())
			.rentable(inventory.getRentable())
			.build();
	}

	private Reservation mapToReservation(final Long memberId, final AddReservationRequest addReservationRequest,
		final List<ReservationSpec> reservationSpecs) {
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
		final RentalPeriod period = new RentalPeriod(addLabRoomReservationRequest.getStartDate(),
			addLabRoomReservationRequest.getEndDate());
		validateLabRoomForReserve(rentable, period);
		final RentalAmount amount = RentalAmount.ofPositive(addLabRoomReservationRequest.getRenterCount());
		remainingQuantityService.validateAmount(rentable.getId(), amount.getAmount(), period);
		final ReservationSpec spec = mapToReservationSpec(rentable, period, amount);
		final Reservation reservation = mapToReservation(memberId, addLabRoomReservationRequest, spec);
		reservationRepository.save(reservation);
		return reservation.getId();
	}

	private void validateLabRoomForReserve(Rentable rentable, RentalPeriod period) {
		if (!rentable.as(LabRoom.class).isAvailable()) {
			throw new LabRoomNotAvailableException();
		}
		labRoomService.validateDays(rentable, period.getRentalDays());
	}

	private ReservationSpec mapToReservationSpec(final Rentable rentable, final RentalPeriod period,
		final RentalAmount amount) {
		return ReservationSpec.builder()
			.period(period)
			.amount(amount)
			.rentable(rentable)
			.build();
	}

	private Reservation mapToReservation(final Long memberId,
		final AddLabRoomReservationRequest addLabRoomReservationRequest, final ReservationSpec spec) {
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
	public ReservationsByEquipmentPerYearMonthResponse getReservationsByEquipmentsPerYearMonth(final Long equipmentId,
		final YearMonth yearMonth) {
		LocalDate startOfMonth = yearMonth.atDay(1);
		LocalDate endOfMonth = yearMonth.atEndOfMonth();
		final List<ReservationSpec> reservationSpecs = reservationSpecRepository.findByStartDateBetween(equipmentId,
			startOfMonth, endOfMonth);
		final ReservationCalendar calendar = ReservationCalendar.from(startOfMonth, endOfMonth);
		calendar.addAll(reservationSpecs);
		return ReservationsByEquipmentPerYearMonthResponse.from(calendar);
	}

	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Set<EquipmentReservationWithMemberNumber> getReservationsByStartDate(final LocalDate startDate) {
		return reservationSpecRepository.findEquipmentReservationWhenAccept(startDate);
	}

	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Map<Long, Set<String>> validatePropertyNumbersCountAndGroupByEquipmentId(final Long reservationId,
		final Map<Long, Set<String>> propertyNumbersByReservationSpecId) {
		final Reservation reservation = reservationRepository.findByIdWithSpecs(reservationId)
			.orElseThrow(ReservationNotFoundException::new);
		validateReservationSpecIdContainsAll(reservation, propertyNumbersByReservationSpecId.keySet());
		Map<Long, Set<String>> collectedByEquipmentId = new HashMap<>();
		for (ReservationSpec reservationSpec : reservation.getReservationSpecs()) {
			reservationSpec.validateAmount(propertyNumbersByReservationSpecId.get(reservationSpec.getId()).size());
			collectedByEquipmentId.put(reservationSpec.getRentable().getId(),
				propertyNumbersByReservationSpecId.get(reservationSpec.getId()));
		}
		return collectedByEquipmentId;
	}

	private void validateReservationSpecIdContainsAll(final Reservation reservation,
		final Set<Long> reservationSpecIdsFromInput) {
		final Set<Long> reservationSpecIdsFromReservation = reservation.getReservationSpecs().stream()
			.filter(ReservationSpec::isReserved)
			.map(ReservationSpec::getId)
			.collect(Collectors.toSet());
		if (reservationSpecIdsFromInput.containsAll(reservationSpecIdsFromReservation) &&
			reservationSpecIdsFromReservation.containsAll(reservationSpecIdsFromInput))
			return;
		throw new ReservationSpecException("신청한 대여 상세와 입력된 대여 상세가 일치하지 않습니다.");
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public List<Reservation> rentLabRoom(final RentLabRoomRequest rentLabRoomRequest) {
		final List<Reservation> reservations = reservationRepository.findByReservationSpecIds(
			rentLabRoomRequest.getReservationSpecIds());
		validateSameLabRoom(rentLabRoomRequest.getName(), reservations);
		for (Reservation reservation : reservations) {
			final LabRoomReservation labRoomReservation = new LabRoomReservation(reservation);
			labRoomReservation.validateWhenRent();
			acceptReservation(labRoomReservation.getId(), List.of(labRoomReservation.getReservationSpecId()));
		}
		return reservations;
	}

	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Set<EquipmentReservationWithMemberNumber> getOverdueReservationsWithMemberNumber(final LocalDate localDate) {
		return reservationSpecRepository.findOverdueEquipmentReservationWhenReturn(localDate);
	}

	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Set<EquipmentReservationWithMemberNumber> getReservationsWithMemberNumberByEndDate(
		final LocalDate localDate) {
		return reservationSpecRepository.findEquipmentReservationWhenReturn(localDate);
	}

	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Reservation getReservationWithReservationSpecsById(final Long id) {
		return reservationRepository.findByIdWithSpecs(id)
			.orElseThrow(ReservationNotFoundException::new);
	}

	@Transactional(readOnly = true)
	public UnterminatedEquipmentReservationsResponse getUnterminatedEquipmentReservations(final Long memberId) {
		final Set<Reservation> reservations = reservationRepository.findNotTerminatedEquipmentReservationsByMemberId(
			memberId);
		final List<Reservation> reservationList = new ArrayList<>(reservations);
		reservationList.sort(Comparator.comparing(Reservation::getRentalPeriod));
		return UnterminatedEquipmentReservationsResponse.from(reservationList);
	}

	@Transactional(readOnly = true)
	public UnterminatedLabRoomReservationsResponse getUnterminatedLabRoomReservations(final Long memberId) {
		final Set<Reservation> reservations = reservationRepository.findNotTerminatedLabRoomReservationsByMemberId(
			memberId);
		final List<Reservation> reservationList = new ArrayList<>(reservations);
		reservationList.sort(Comparator.comparing(Reservation::getRentalPeriod));
		return UnterminatedLabRoomReservationsResponse.from(reservationList);
	}

	@Transactional
	public Long cancelReservationSpec(final Long reservationSpecId, final Integer amount) {
		final ReservationSpec reservationSpec = getReservationSpec(reservationSpecId);
		cancelAndAdjust(reservationSpec, amount);
		return reservationSpec.getId();
	}

	private ReservationSpec getReservationSpec(final Long reservationSpecId) {
		return reservationSpecRepository.findById(reservationSpecId)
			.orElseThrow(ReservationSpecNotFoundException::new);
	}

	private void cancelAndAdjust(final ReservationSpec reservationSpec, final Integer amount) {
		reservationSpec.cancelAmount(amount);
		reservationSpecRepository.adjustAmountAndStatus(reservationSpec);
		final Long reservationId = reservationSpec.getReservation().getId();
		updateAndAdjustTerminated(reservationId);
	}

	private void updateAndAdjustTerminated(final Long reservationId) {
		final Reservation reservation = reservationRepository.findByIdWithSpecs(reservationId)
			.orElseThrow(ReservationNotFoundException::new);
		reservation.updateIfTerminated();
		if (reservation.isTerminated())
			reservationRepository.adjustTerminated(reservation);
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
		final Reservation reservation = getReservationById(id);
		reservation.acceptAt(LocalDateTime.now());
		reservationSpecRepository.updateStatusByIds(rentedReservationSpecIds, ReservationSpecStatus.RENTED);
	}

	private Reservation getReservationById(Long id) {
		return reservationRepository.findById(id)
			.orElseThrow(ReservationNotFoundException::new);
	}

	private void validateSameLabRoom(final String labRoomName, final List<Reservation> reservations) {
		final boolean isSameRentable = reservations.stream()
			.allMatch(reservation -> reservation.isOnlyRentFor(labRoomName));
		if (!isSameRentable)
			throw new NotSameRentableRentException();
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public List<Reservation> returnLabRoom(final ReturnLabRoomRequest returnLabRoomRequest) {
		final List<Reservation> reservations = reservationRepository.findByReservationSpecIds(
			returnLabRoomRequest.getReservationSpecIds());
		validateSameLabRoom(returnLabRoomRequest.getName(), reservations);
		for (Reservation reservation : reservations) {
			final LabRoomReservation labRoomReservation = new LabRoomReservation(reservation);
			labRoomReservation.validateWhenReturn();
			labRoomReservation.normalReturnAll();
		}
		return reservations;
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void cancelAll(final Long memberId) {
		Set<Reservation> reservations = reservationRepository.findNotTerminatedReservationsByMemberId(memberId);
		final List<ReservationSpec> specs = reservations.stream()
			.filter(reservation -> !reservation.isAccepted())
			.map(Reservation::getReservationSpecs)
			.flatMap(Collection::stream)
			.filter(ReservationSpec::isReserved)
			.toList();
		specs.forEach(spec -> cancelAndAdjust(spec, spec.getAmount().getAmount()));
	}

	public RelatedReservationsInfoResponse getRelatedReservationsInfo(Long id) {
		Reservation reservation = getReservationById(id);
		LabRoomReservation labRoomReservation = new LabRoomReservation(reservation);
		final List<Reservation> reservations = reservationRepository.findRelatedReservation(labRoomReservation);
		return RelatedReservationsInfoResponse.from(reservations);
	}

	@Transactional(readOnly = true)
	public HistoryStatResponse getHistoryStat(String name, LocalDate startDate, LocalDate endDate) {
		return reservationSpecRepository.findHistoryStat(name, startDate, endDate);
	}
}
