package com.girigiri.kwrental.reservation.service.remainquantity;

import static java.util.stream.Collectors.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.service.RemainingQuantityService;
import com.girigiri.kwrental.reservation.domain.OperatingPeriod;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.ReservedAmount;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RemainingQuantityServiceImpl implements RemainingQuantityService {

	private final ReservationSpecRepository reservationSpecRepository;

	@Override
	@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
	public Map<Long, Integer> getRemainingQuantityByAssetIdAndDate(final List<Long> rentableIds,
		final LocalDate date) {
		return reservationSpecRepository.findRentalAmountsByAssetIds(rentableIds, date)
			.stream()
			.collect(toMap(ReservedAmount::getEquipmentId, ReservedAmount::getRemainingAmount));
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
