package com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;

import lombok.Getter;

@Getter
public class EquipmentReservationWithRentalSpecsResponse {

	private Long reservationId;
	private String name;
	private String memberNumber;
	private LocalDateTime acceptDateTime;
	private List<ReservationSpecWithRentalSpecsResponse> reservationSpecs;

	private EquipmentReservationWithRentalSpecsResponse() {
	}

	private EquipmentReservationWithRentalSpecsResponse(final Long reservationId, final String name,
		final String memberNumber, final LocalDateTime acceptDateTime,
		final List<ReservationSpecWithRentalSpecsResponse> reservationSpecs) {
		this.reservationId = reservationId;
		this.name = name;
		this.memberNumber = memberNumber;
		this.acceptDateTime = acceptDateTime;
		this.reservationSpecs = reservationSpecs;
	}

	public static EquipmentReservationWithRentalSpecsResponse of(
		final EquipmentReservationWithMemberNumber equipmentReservationWithMemberNumber,
		final List<EquipmentRentalSpec> rentalSpecs) {
		final List<ReservationSpecWithRentalSpecsResponse> reservationSpecWithRentalSpecsResponse = mapToReservationSpecWithRentalSpecResponse(
			rentalSpecs, equipmentReservationWithMemberNumber);
		final LocalDateTime acceptDateTime = equipmentReservationWithMemberNumber.getAcceptDateTime() == null ? null :
			equipmentReservationWithMemberNumber.getAcceptDateTime().toLocalDateTime();
		return new EquipmentReservationWithRentalSpecsResponse(equipmentReservationWithMemberNumber.getId(),
			equipmentReservationWithMemberNumber.getRenterName(),
			equipmentReservationWithMemberNumber.getMemberNumber(), acceptDateTime,
			reservationSpecWithRentalSpecsResponse);
	}

	private static List<ReservationSpecWithRentalSpecsResponse> mapToReservationSpecWithRentalSpecResponse(
		final List<EquipmentRentalSpec> rentalSpecs,
		final EquipmentReservationWithMemberNumber equipmentReservationWithMemberNumber) {
		final Map<Long, List<EquipmentRentalSpec>> groupedRentalSpecsByReservationSpecId = rentalSpecs.stream()
			.collect(groupingBy(EquipmentRentalSpec::getReservationSpecId));
		return equipmentReservationWithMemberNumber.getReservationSpecs().stream()
			.map(it -> ReservationSpecWithRentalSpecsResponse.of(it,
				groupedRentalSpecsByReservationSpecId.get(it.getId())))
			.toList();
	}
}
