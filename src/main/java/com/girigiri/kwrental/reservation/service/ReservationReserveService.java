package com.girigiri.kwrental.reservation.service;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.girigiri.kwrental.asset.domain.Rentable;
import com.girigiri.kwrental.asset.labroom.domain.LabRoom;
import com.girigiri.kwrental.asset.labroom.exception.LabRoomNotAvailableException;
import com.girigiri.kwrental.asset.labroom.service.LabRoomService;
import com.girigiri.kwrental.asset.service.AssetService;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.inventory.domain.RentalAmount;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.inventory.service.InventoryService;
import com.girigiri.kwrental.reservation.domain.LabRoomReservation;
import com.girigiri.kwrental.reservation.domain.Reservation;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.dto.request.AddEquipmentReservationRequest;
import com.girigiri.kwrental.reservation.dto.request.AddLabRoomReservationRequest;
import com.girigiri.kwrental.reservation.exception.AlreadyReservedLabRoomException;
import com.girigiri.kwrental.reservation.exception.ReservationException;
import com.girigiri.kwrental.reservation.repository.ReservationRepository;

@Service
public class ReservationReserveService {

	private final ReservationRepository reservationRepository;
	private final InventoryService inventoryService;
	private final PenaltyService penaltyService;
	private final RemainingQuantityServiceImpl remainingQuantityService;
	private final AssetService assetService;
	private final LabRoomService labRoomService;

	public ReservationReserveService(final ReservationRepository reservationRepository,
		final InventoryService inventoryService, final PenaltyService penaltyService,
		final RemainingQuantityServiceImpl remainingQuantityService, final AssetService assetService,
		final LabRoomService labRoomService) {
		this.reservationRepository = reservationRepository;
		this.inventoryService = inventoryService;
		this.penaltyService = penaltyService;
		this.remainingQuantityService = remainingQuantityService;
		this.assetService = assetService;
		this.labRoomService = labRoomService;
	}

	void reserveEquipment(final Long memberId, final AddEquipmentReservationRequest addReservationRequest) {
		validatePenalty(memberId);
		final List<Inventory> inventories = inventoryService.getInventoriesWithEquipment(memberId);
		final List<ReservationSpec> reservationSpecs = inventories.stream()
			.filter(this::isAvailableCountValid)
			.map(this::mapToReservationSpec)
			.toList();
		final List<Reservation> reservations = mapToReservations(memberId, addReservationRequest, reservationSpecs);
		inventoryService.deleteAll(memberId);
		reservationRepository.saveAll(reservations);
	}

	private void validatePenalty(final Long memberId) {
		if (penaltyService.hasOngoingPenalty(memberId)) {
			throw new ReservationException("페널티에 적용되는 기간에는 대여 예약을 할 수 없습니다.");
		}
	}

	private boolean isAvailableCountValid(final Inventory inventory) {
		remainingQuantityService.validateAmount(inventory.getRentable().getId(),
			inventory.getRentalAmount().getAmount(), inventory.getRentalPeriod());
		return true;
	}

	private ReservationSpec mapToReservationSpec(final Inventory inventory) {
		return ReservationSpec.builder()
			.period(inventory.getRentalPeriod())
			.amount(inventory.getRentalAmount())
			.rentable(inventory.getRentable())
			.build();
	}

	private List<Reservation> mapToReservations(final Long memberId,
		final AddEquipmentReservationRequest addReservationRequest,
		final List<ReservationSpec> reservationSpecs) {
		final Map<RentalPeriod, List<ReservationSpec>> collectByPeriod = reservationSpecs.stream()
			.collect(groupingBy(ReservationSpec::getPeriod));
		return collectByPeriod.keySet()
			.stream()
			.map(key -> mapToReservation(memberId, addReservationRequest, collectByPeriod.get(key)))
			.toList();
	}

	private Reservation mapToReservation(final Long memberId,
		final AddEquipmentReservationRequest addReservationRequest,
		final List<ReservationSpec> samePeriodSpec) {
		return Reservation.builder()
			.reservationSpecs(samePeriodSpec)
			.email(addReservationRequest.renterEmail())
			.name(addReservationRequest.renterName())
			.purpose(addReservationRequest.rentalPurpose())
			.phoneNumber(addReservationRequest.renterPhoneNumber())
			.memberId(memberId)
			.build();
	}

	Long reserveLabRoom(final Long memberId, final AddLabRoomReservationRequest addLabRoomReservationRequest) {
		validatePenalty(memberId);
		final Rentable rentable = assetService.getRentableByName(addLabRoomReservationRequest.labRoomName());
		final RentalPeriod period = new RentalPeriod(addLabRoomReservationRequest.startDate(),
			addLabRoomReservationRequest.endDate());
		validateAlreadyReserved(memberId, period);
		validateLabRoomForReserve(rentable, period);
		final RentalAmount amount = RentalAmount.ofPositive(addLabRoomReservationRequest.renterCount());
		remainingQuantityService.validateAmount(rentable.getId(), amount.getAmount(), period);
		final ReservationSpec spec = mapToReservationSpec(rentable, period, amount);
		final Reservation reservation = mapToReservation(memberId, addLabRoomReservationRequest, spec);
		reservationRepository.save(reservation);
		return reservation.getId();
	}

	private void validateAlreadyReserved(final Long memberId, final RentalPeriod period) {
		boolean alreadyReserved = reservationRepository.findNotTerminatedLabRoomReservationsByMemberId(
				memberId).stream()
			.map(LabRoomReservation::new)
			.anyMatch(it -> it.has(period));
		if (alreadyReserved) {
			throw new AlreadyReservedLabRoomException();
		}
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
			.email(addLabRoomReservationRequest.renterEmail())
			.name(addLabRoomReservationRequest.renterName())
			.purpose(addLabRoomReservationRequest.rentalPurpose())
			.phoneNumber(addLabRoomReservationRequest.renterPhoneNumber())
			.build();
	}
}
