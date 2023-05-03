package com.girigiri.kwrental.rental.dto.response;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ReservationSpecByStartDateResponse {

    private Long reservationSpecId;
    private String imgUrl;
    private String category;
    private String modelName;
    private Integer amount;
    private List<RentalSpecByStartDateResponse> rentalSpecs;

    private ReservationSpecByStartDateResponse() {
    }

    @Builder
    private ReservationSpecByStartDateResponse(final Long reservationSpecId, final String imgUrl, final String category, final String modelName, final Integer amount, final List<RentalSpecByStartDateResponse> rentalSpecs) {
        this.reservationSpecId = reservationSpecId;
        this.imgUrl = imgUrl;
        this.category = category;
        this.modelName = modelName;
        this.amount = amount;
        this.rentalSpecs = rentalSpecs;
    }

    public static ReservationSpecByStartDateResponse of(final ReservationSpec reservationSpec, final List<RentalSpec> rentalSpecs) {
        final Equipment equipment = reservationSpec.getEquipment();
        final List<RentalSpecByStartDateResponse> rentalSpecByStartDateResponses = mapToRentalSpecDto(rentalSpecs);
        return ReservationSpecByStartDateResponse.builder()
                .reservationSpecId(reservationSpec.getId())
                .imgUrl(equipment.getImgUrl())
                .category(equipment.getCategory().name())
                .modelName(equipment.getModelName())
                .amount(reservationSpec.getAmount().getAmount())
                .rentalSpecs(rentalSpecByStartDateResponses)
                .build();
    }

    private static List<RentalSpecByStartDateResponse> mapToRentalSpecDto(final List<RentalSpec> rentalSpecs) {
        if (rentalSpecs == null) return null;
        return rentalSpecs.stream()
                .map(RentalSpecByStartDateResponse::from)
                .toList();
    }
}
