package com.girigiri.kwrental.reservation.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.girigiri.kwrental.asset.equipment.domain.Category;
import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.reservation.domain.entity.Reservation;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpecStatus;

public record UnterminatedEquipmentReservationsResponse(
    List<UnterminatedEquipmentReservationResponse> reservations
) {
    public record UnterminatedEquipmentReservationResponse(
        LocalDate startDate,
        LocalDate endDate,
        List<UnterminatedEquipmentReservationResponse.UnterminatedEquipmentReservationSpecResponse> reservationSpecs
    ) {
        public static UnterminatedEquipmentReservationResponse from(final Reservation unterminatedReservation) {
            final List<UnterminatedEquipmentReservationResponse.UnterminatedEquipmentReservationSpecResponse> reservationSpecResponses = unterminatedReservation.getReservationSpecs()
                .stream()
                .map(UnterminatedEquipmentReservationResponse.UnterminatedEquipmentReservationSpecResponse::from)
                .toList();
            return new UnterminatedEquipmentReservationResponse(unterminatedReservation.getStartDate(),
                unterminatedReservation.getEndDate(), reservationSpecResponses);
        }

        public record UnterminatedEquipmentReservationSpecResponse(
            Long id,
            Category category,
            String modelName,
            String imgUrl,
            Integer rentalAmount,
            ReservationSpecStatus status
        ) {
            public static UnterminatedEquipmentReservationResponse.UnterminatedEquipmentReservationSpecResponse from(
                final ReservationSpec reservationSpec) {
                final Equipment equipment = reservationSpec.getAsset().as(Equipment.class);
                return new UnterminatedEquipmentReservationResponse.UnterminatedEquipmentReservationSpecResponse(
                    reservationSpec.getId(), equipment.getCategory(), equipment.getName(), equipment.getImgUrl(),
                    reservationSpec.getAmount().getAmount(), reservationSpec.getStatus());
            }
        }
    }

    public static UnterminatedEquipmentReservationsResponse from(final List<Reservation> unterminatedReservations) {
        final List<UnterminatedEquipmentReservationResponse> reservationResponses = unterminatedReservations.stream()
            .map(UnterminatedEquipmentReservationResponse::from)
            .toList();
        return new UnterminatedEquipmentReservationsResponse(reservationResponses);
    }
}
