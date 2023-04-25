package com.girigiri.kwrental.reservation.dto.response;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReservationSpecResponse {

    private String imgUrl;
    private String category;
    private String modelName;
    private Integer amount;

    private ReservationSpecResponse() {
    }

    @Builder
    private ReservationSpecResponse(final String imgUrl, final String category, final String modelName, final Integer amount) {
        this.imgUrl = imgUrl;
        this.category = category;
        this.modelName = modelName;
        this.amount = amount;
    }

    public static ReservationSpecResponse from(final ReservationSpec reservationSpec) {
        final Equipment equipment = reservationSpec.getEquipment();
        return ReservationSpecResponse.builder()
                .imgUrl(equipment.getImgUrl())
                .category(equipment.getCategory().name())
                .modelName(equipment.getModelName())
                .amount(reservationSpec.getAmount().getAmount())
                .build();
    }
}
