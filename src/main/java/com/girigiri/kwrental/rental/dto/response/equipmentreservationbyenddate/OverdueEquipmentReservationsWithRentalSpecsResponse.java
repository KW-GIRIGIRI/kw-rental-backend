package com.girigiri.kwrental.rental.dto.response.equipmentreservationbyenddate;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;

import lombok.Builder;

public record OverdueEquipmentReservationsWithRentalSpecsResponse(
	List<OverdueEquipmentReservationResponse> reservations) {

	public static OverdueEquipmentReservationsWithRentalSpecsResponse of(
		final Set<EquipmentReservationWithMemberNumber> equipmentReservations,
		final List<EquipmentRentalSpec> rentalSpecs) {
		final List<OverdueEquipmentReservationResponse> reservationResponses = equipmentReservations.stream()
			.map(it -> OverdueEquipmentReservationResponse.of(it, rentalSpecs))
			.toList();
		return new OverdueEquipmentReservationsWithRentalSpecsResponse(reservationResponses);
	}

	public record OverdueEquipmentReservationResponse(
		Long reservationId,
		String name,
		String memberNumber,
		LocalDateTime overdueAcceptDateTime,
		List<OverdueEquipmentReservationSpecResponse> reservationSpecs) {

		public static OverdueEquipmentReservationResponse of(
			final EquipmentReservationWithMemberNumber equipmentReservation,
			final List<EquipmentRentalSpec> rentalSpecs) {
			final List<OverdueEquipmentReservationSpecResponse> overdueEquipmentReservationSpecRespons = mapToReservationSpecResponse(
				rentalSpecs, equipmentReservation);
			final RentalDateTime acceptDateTime = equipmentReservation.getAcceptDateTime();
			return new OverdueEquipmentReservationResponse(equipmentReservation.getId(),
				equipmentReservation.getRenterName(),
				equipmentReservation.getMemberNumber(),
				acceptDateTime == null ? null : acceptDateTime.toLocalDateTime(),
				overdueEquipmentReservationSpecRespons);
		}

		private static List<OverdueEquipmentReservationSpecResponse> mapToReservationSpecResponse(
			final List<EquipmentRentalSpec> rentalSpecs,
			final EquipmentReservationWithMemberNumber equipmentReservation) {
			final Map<Long, List<EquipmentRentalSpec>> groupedRentalSpecsByReservationSpecId = rentalSpecs.stream()
				.collect(groupingBy(EquipmentRentalSpec::getReservationSpecId));
			return equipmentReservation.getReservationSpecs().stream()
				.filter(it -> groupedRentalSpecsByReservationSpecId.get(it.getId()) != null)
				.map(it -> OverdueEquipmentReservationSpecResponse.of(it,
					groupedRentalSpecsByReservationSpecId.get(it.getId())))
				.toList();
		}

		@Builder
		public record OverdueEquipmentReservationSpecResponse(
			Long reservationSpecId,
			Long equipmentId,
			String imgUrl,
			String category,
			String modelName,
			Integer amount,
			List<OverdueEquipmentRentalSpecResponse> rentalSpecs) {

			public static OverdueEquipmentReservationSpecResponse of(final ReservationSpec reservationSpec,
				final List<EquipmentRentalSpec> rentalSpecs) {
				final Equipment equipment = reservationSpec.getRentable().as(Equipment.class);
				final List<OverdueEquipmentRentalSpecResponse> rentalSpecByStartDateResponses = mapToRentalSpecDto(
					rentalSpecs);
				return OverdueEquipmentReservationSpecResponse.builder()
					.reservationSpecId(reservationSpec.getId())
					.equipmentId(equipment.getId())
					.imgUrl(equipment.getImgUrl())
					.category(equipment.getCategory().name())
					.modelName(equipment.getName())
					.amount(rentalSpecByStartDateResponses.size())
					.rentalSpecs(rentalSpecByStartDateResponses)
					.build();
			}

			private static List<OverdueEquipmentRentalSpecResponse> mapToRentalSpecDto(
				final List<EquipmentRentalSpec> rentalSpecs) {
				return rentalSpecs.stream()
					.map(OverdueEquipmentRentalSpecResponse::from)
					.toList();
			}

			public record OverdueEquipmentRentalSpecResponse(
				Long rentalSpecId,
				String propertyNumber) {

				public static OverdueEquipmentRentalSpecResponse from(
					final EquipmentRentalSpec equipmentRentalSpec) {
					return new OverdueEquipmentRentalSpecResponse(
						equipmentRentalSpec.getId(), equipmentRentalSpec.getPropertyNumber());
				}
			}
		}
	}
}
