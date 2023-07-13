package com.girigiri.kwrental.reservation.service;

import static java.util.stream.Collectors.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.domain.Rentable;
import com.girigiri.kwrental.asset.service.AssetService;
import com.girigiri.kwrental.asset.service.RemainingQuantityService;
import com.girigiri.kwrental.inventory.domain.RentalPeriod;
import com.girigiri.kwrental.inventory.service.AmountValidator;
import com.girigiri.kwrental.reservation.domain.OperatingPeriod;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.ReservedAmount;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RemainingQuantityServiceImpl implements RemainingQuantityService, AmountValidator {

	private final ReservationSpecRepository reservationSpecRepository;
	private final AssetService assetService;

	@Override
	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Map<Long, Integer> getRemainingQuantityByAssetIdAndDate(final List<Long> rentableIds,
		final LocalDate date) {
		return reservationSpecRepository.findRentalAmountsByAssetIds(rentableIds, date)
			.stream()
			.collect(toMap(ReservedAmount::getEquipmentId, ReservedAmount::getRemainingAmount));
	}

	@Override   // TODO: 2023/04/23 반복문을 두번 도는 로직을 최적화 할 수 있다.
	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public void validateAmount(final Long assetId, final Integer amount, final RentalPeriod rentalPeriod) {
		final Rentable rentable = assetService.getRentableById(assetId);
		final List<ReservationSpec> overlappedReservationSpecs = reservationSpecRepository.findOverlappedReservedOrRentedByPeriod(
			assetId, rentalPeriod);
		for (LocalDate i = rentalPeriod.getRentalStartDate(); i.isBefore(
			rentalPeriod.getRentalEndDate()); i = i.plusDays(1)) {
			final int rentedAmountByDate = sumRentedAmountByDate(overlappedReservationSpecs, i);
			rentable.validateAmountForRent(amount + rentedAmountByDate);
		}
	}

	private int sumRentedAmountByDate(final List<ReservationSpec> overlappedReservationSpecs, final LocalDate date) {
		return overlappedReservationSpecs.stream()
			.filter(spec -> spec.containsDate(date))
			.mapToInt(spec -> spec.getAmount().getAmount())
			.sum();
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Map<LocalDate, Integer> getReservedAmountInclusive(
		final Long rentableId, final LocalDate from, final LocalDate to) {
		final List<ReservationSpec> overlappedSpecs =
			reservationSpecRepository.findOverlappedReservedOrRentedInclusive(rentableId, from, to);
		final OperatingPeriod operatingPeriod = new OperatingPeriod(from, to);
		return operatingPeriod.getRentalAvailableDates().stream()
			.collect(toMap(Function.identity(), date -> getReservedAmountsByDate(overlappedSpecs, date)));
	}

	private int getReservedAmountsByDate(final List<ReservationSpec> reservationSpecs, final LocalDate date) {
		return reservationSpecs.stream()
			.filter(it -> it.containsDate(date))
			.mapToInt(it -> it.getAmount().getAmount())
			.sum();
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Map<LocalDate, Integer> getReservationCountInclusive(
		final Long rentableId, final LocalDate from, final LocalDate to) {
		List<ReservationSpec> overlappedSpecs = reservationSpecRepository.findOverlappedReservedOrRentedInclusive(
			rentableId, from, to);
		final OperatingPeriod operatingPeriod = new OperatingPeriod(from, to);
		return operatingPeriod.getRentalAvailableDates().stream()
			.collect(toMap(Function.identity(), date -> getSpecCount(overlappedSpecs, date)));
	}

	private int getSpecCount(List<ReservationSpec> specs, LocalDate date) {
		return (int)specs.stream()
			.filter(it -> it.containsDate(date))
			.count();
	}
}
