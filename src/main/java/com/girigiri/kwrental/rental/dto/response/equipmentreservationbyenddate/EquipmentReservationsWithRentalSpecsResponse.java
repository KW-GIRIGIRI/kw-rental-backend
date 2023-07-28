package com.girigiri.kwrental.rental.dto.response.equipmentreservationbyenddate;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.rental.domain.entity.EquipmentRentalSpec;
import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;

import lombok.Builder;

public record EquipmentReservationsWithRentalSpecsResponse(
	List<EquipmentReservationWithRentalSpecsResponse> reservations
) {

	public static EquipmentReservationsWithRentalSpecsResponse of(
		final Set<EquipmentReservationWithMemberNumber> reservations, final List<EquipmentRentalSpec> rentalSpecs) {
		final List<EquipmentReservationWithRentalSpecsResponse> equipmentReservationWithRentalSpecsRespons = reservations.stream()
			.map(it -> EquipmentReservationWithRentalSpecsResponse.of(it, rentalSpecs))
			.toList();
		return new EquipmentReservationsWithRentalSpecsResponse(equipmentReservationWithRentalSpecsRespons);
	}

	public record EquipmentReservationWithRentalSpecsResponse(

		Long reservationId,
		String name,
		String memberNumber,
		LocalDateTime acceptDateTime,
		List<EquipmentReservationSpecWithRentalSpecsResponse> reservationSpecs
	) {
		public static EquipmentReservationWithRentalSpecsResponse of(
			final EquipmentReservationWithMemberNumber equipmentReservationWithMemberNumber,
			final List<EquipmentRentalSpec> rentalSpecs) {
			final List<EquipmentReservationSpecWithRentalSpecsResponse> equipmentReservationSpecWithRentalSpecsResponse = mapToReservationSpecWithRentalSpecResponse(
				rentalSpecs, equipmentReservationWithMemberNumber);
			final LocalDateTime acceptDateTime =
				equipmentReservationWithMemberNumber.getAcceptDateTime() == null ? null :
					equipmentReservationWithMemberNumber.getAcceptDateTime().toLocalDateTime();
			return new EquipmentReservationWithRentalSpecsResponse(equipmentReservationWithMemberNumber.getId(),
				equipmentReservationWithMemberNumber.getRenterName(),
				equipmentReservationWithMemberNumber.getMemberNumber(), acceptDateTime,
				equipmentReservationSpecWithRentalSpecsResponse);
		}

		private static List<EquipmentReservationSpecWithRentalSpecsResponse> mapToReservationSpecWithRentalSpecResponse(
			final List<EquipmentRentalSpec> rentalSpecs,
			final EquipmentReservationWithMemberNumber equipmentReservationWithMemberNumber) {
			final Map<Long, List<EquipmentRentalSpec>> groupedRentalSpecsByReservationSpecId = rentalSpecs.stream()
				.collect(groupingBy(EquipmentRentalSpec::getReservationSpecId));
			return equipmentReservationWithMemberNumber.getReservationSpecs().stream()
				.map(it -> EquipmentReservationSpecWithRentalSpecsResponse.of(it,
					groupedRentalSpecsByReservationSpecId.get(it.getId())))
				.toList();
		}

		@Builder
		public record EquipmentReservationSpecWithRentalSpecsResponse(

			Long reservationSpecId,
			Long equipmentId,
			String imgUrl,
			String category,
			String modelName,
			Integer amount,
			List<EquipmentRentalSpecResponse> rentalSpecs) {

			public static EquipmentReservationSpecWithRentalSpecsResponse of(final ReservationSpec reservationSpec,
				final List<EquipmentRentalSpec> rentalSpecs) {
				final Equipment equipment = reservationSpec.getRentable().as(Equipment.class);
				final List<EquipmentRentalSpecResponse> rentalSpecByStartDateResponses = mapToRentalSpecDto(
					rentalSpecs);
				return EquipmentReservationSpecWithRentalSpecsResponse.builder()
					.reservationSpecId(reservationSpec.getId())
					.imgUrl(equipment.getImgUrl())
					.equipmentId(equipment.getId())
					.category(equipment.getCategory().name())
					.modelName(equipment.getName())
					.amount(reservationSpec.getAmount().getAmount())
					.rentalSpecs(rentalSpecByStartDateResponses)
					.build();
			}

			private static List<EquipmentRentalSpecResponse> mapToRentalSpecDto(
				final List<EquipmentRentalSpec> rentalSpecs) {
				if (rentalSpecs == null)
					return null;
				return rentalSpecs.stream()
					.map(EquipmentRentalSpecResponse::from)
					.toList();
			}

			@Builder
			public record EquipmentRentalSpecResponse(
				Long rentalSpecId,
				String propertyNumber) {

				public static EquipmentRentalSpecResponse from(final EquipmentRentalSpec equipmentRentalSpec) {
					return EquipmentRentalSpecResponse.builder()
						.rentalSpecId(equipmentRentalSpec.getId())
						.propertyNumber(equipmentRentalSpec.getPropertyNumber())
						.build();
				}
			}
		}
	}
}
