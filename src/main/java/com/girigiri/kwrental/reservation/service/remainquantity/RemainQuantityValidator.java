package com.girigiri.kwrental.reservation.service.remainquantity;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.domain.Rentable;
import com.girigiri.kwrental.asset.service.AssetService;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.repository.ReservationSpecRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RemainQuantityValidator {
	private final AssetService assetService;
	private final ReservationSpecRepository reservationSpecRepository;

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
}
