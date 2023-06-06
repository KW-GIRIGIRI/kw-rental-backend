package com.girigiri.kwrental.reservation.dto.response;

import com.girigiri.kwrental.asset.equipment.domain.Category;
import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.ReservationSpecStatus;

import lombok.Getter;

@Getter
public class UnterminatedEquipmentReservationSpecResponse {

    private Long id;

    private Category category;
    private String modelName;
    private String imgUrl;
    private Integer rentalAmount;
    private ReservationSpecStatus status;

    private UnterminatedEquipmentReservationSpecResponse() {
    }

    private UnterminatedEquipmentReservationSpecResponse(final Long id, final Category category, final String modelName, final String imgUrl, final Integer rentalAmount, final ReservationSpecStatus status) {
        this.id = id;
        this.category = category;
        this.modelName = modelName;
        this.imgUrl = imgUrl;
        this.rentalAmount = rentalAmount;
        this.status = status;
    }

    public static UnterminatedEquipmentReservationSpecResponse from(final ReservationSpec reservationSpec) {
        final Equipment equipment = reservationSpec.getRentable().as(Equipment.class);
        return new UnterminatedEquipmentReservationSpecResponse(
                reservationSpec.getId(), equipment.getCategory(), equipment.getName(), equipment.getImgUrl(), reservationSpec.getAmount().getAmount(), reservationSpec.getStatus());
    }
}
