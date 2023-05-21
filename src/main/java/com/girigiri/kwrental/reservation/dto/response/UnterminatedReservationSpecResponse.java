package com.girigiri.kwrental.reservation.dto.response;

import com.girigiri.kwrental.equipment.domain.Category;
import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import com.girigiri.kwrental.reservation.domain.ReservationSpecStatus;
import lombok.Getter;

@Getter
public class UnterminatedReservationSpecResponse {

    private Long id;

    private Category category;
    private String modelName;
    private String imgUrl;
    private Integer rentalAmount;
    private ReservationSpecStatus status;

    private UnterminatedReservationSpecResponse() {
    }

    private UnterminatedReservationSpecResponse(final Long id, final Category category, final String modelName, final String imgUrl, final Integer rentalAmount, final ReservationSpecStatus status) {
        this.id = id;
        this.category = category;
        this.modelName = modelName;
        this.imgUrl = imgUrl;
        this.rentalAmount = rentalAmount;
        this.status = status;
    }

    public static UnterminatedReservationSpecResponse from(final ReservationSpec reservationSpec) {
        final Equipment equipment = reservationSpec.getEquipment();
        return new UnterminatedReservationSpecResponse(
                reservationSpec.getId(), equipment.getCategory(), equipment.getName(), equipment.getImgUrl(), reservationSpec.getAmount().getAmount(), reservationSpec.getStatus());
    }
}
