package com.girigiri.kwrental.reservation.dto.response;

import com.girigiri.kwrental.asset.equipment.domain.Category;
import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.entity.ReservationSpecStatus;

public record UnterminatedEquipmentReservationSpecResponse(

    Long id,
    Category category,
    String modelName,
    String imgUrl,
    Integer rentalAmount,
    ReservationSpecStatus status
) {
    public static UnterminatedEquipmentReservationSpecResponse from(final ReservationSpec reservationSpec) {
        final Equipment equipment = reservationSpec.getRentable().as(Equipment.class);
        return new UnterminatedEquipmentReservationSpecResponse(
            reservationSpec.getId(), equipment.getCategory(), equipment.getName(), equipment.getImgUrl(),
            reservationSpec.getAmount().getAmount(), reservationSpec.getStatus());
    }
}
