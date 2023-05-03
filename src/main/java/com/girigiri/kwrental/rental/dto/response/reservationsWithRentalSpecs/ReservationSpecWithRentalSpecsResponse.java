package com.girigiri.kwrental.rental.dto.response.reservationsWithRentalSpecs;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ReservationSpecWithRentalSpecsResponse {

    private Long reservationSpecId;
    private String imgUrl;
    private String category;
    private String modelName;
    private Integer amount;
    private List<RentalSpecResponse> rentalSpecs;

    private ReservationSpecWithRentalSpecsResponse() {
    }

    @Builder
    private ReservationSpecWithRentalSpecsResponse(final Long reservationSpecId, final String imgUrl, final String category, final String modelName, final Integer amount, final List<RentalSpecResponse> rentalSpecs) {
        this.reservationSpecId = reservationSpecId;
        this.imgUrl = imgUrl;
        this.category = category;
        this.modelName = modelName;
        this.amount = amount;
        this.rentalSpecs = rentalSpecs;
    }

    public static ReservationSpecWithRentalSpecsResponse of(final ReservationSpec reservationSpec, final List<RentalSpec> rentalSpecs) {
        final Equipment equipment = reservationSpec.getEquipment();
        final List<RentalSpecResponse> rentalSpecByStartDateResponses = mapToRentalSpecDto(rentalSpecs);
        return ReservationSpecWithRentalSpecsResponse.builder()
                .reservationSpecId(reservationSpec.getId())
                .imgUrl(equipment.getImgUrl())
                .category(equipment.getCategory().name())
                .modelName(equipment.getModelName())
                .amount(reservationSpec.getAmount().getAmount())
                .rentalSpecs(rentalSpecByStartDateResponses)
                .build();
    }

    private static List<RentalSpecResponse> mapToRentalSpecDto(final List<RentalSpec> rentalSpecs) {
        if (rentalSpecs == null) return null;
        return rentalSpecs.stream()
                .map(RentalSpecResponse::from)
                .toList();
    }
}
