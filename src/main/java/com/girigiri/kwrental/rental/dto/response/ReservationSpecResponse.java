package com.girigiri.kwrental.rental.dto.response;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ReservationSpecResponse {

    private Long reservationSpecId;
    private String imgUrl;
    private String category;
    private String modelName;
    private Integer amount;
    private List<RentalSpecResponse> rentalSpecs;

    private ReservationSpecResponse() {
    }

    @Builder
    private ReservationSpecResponse(final Long reservationSpecId, final String imgUrl, final String category, final String modelName, final Integer amount, final List<RentalSpecResponse> rentalSpecs) {
        this.reservationSpecId = reservationSpecId;
        this.imgUrl = imgUrl;
        this.category = category;
        this.modelName = modelName;
        this.amount = amount;
        this.rentalSpecs = rentalSpecs;
    }

    public static ReservationSpecResponse of(final ReservationSpec reservationSpec, final List<RentalSpecResponse> rentalSpecResponses) {
        final Equipment equipment = reservationSpec.getEquipment();
        return ReservationSpecResponse.builder()
                .reservationSpecId(reservationSpec.getId())
                .imgUrl(equipment.getImgUrl())
                .category(equipment.getCategory().name())
                .modelName(equipment.getModelName())
                .amount(reservationSpec.getAmount().getAmount())
                .rentalSpecs(rentalSpecResponses)
                .build();
    }
}
