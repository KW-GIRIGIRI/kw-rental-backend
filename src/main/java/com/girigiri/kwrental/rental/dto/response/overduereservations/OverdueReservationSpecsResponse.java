package com.girigiri.kwrental.rental.dto.response.overduereservations;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class OverdueReservationSpecsResponse {

    private Long reservationSpecId;
    private String imgUrl;
    private String category;
    private String modelName;
    private Integer amount;
    private List<OverdueRentalSpecResponse> rentalSpecs;

    private OverdueReservationSpecsResponse() {
    }

    @Builder
    private OverdueReservationSpecsResponse(final Long reservationSpecId, final String imgUrl, final String category, final String modelName, final Integer amount, final List<OverdueRentalSpecResponse> rentalSpecs) {
        this.reservationSpecId = reservationSpecId;
        this.imgUrl = imgUrl;
        this.category = category;
        this.modelName = modelName;
        this.amount = amount;
        this.rentalSpecs = rentalSpecs;
    }

    public static OverdueReservationSpecsResponse of(final ReservationSpec reservationSpec, final List<RentalSpec> rentalSpecs) {
        final Equipment equipment = reservationSpec.getEquipment();
        final List<OverdueRentalSpecResponse> rentalSpecByStartDateResponses = mapToRentalSpecDto(rentalSpecs);
        return OverdueReservationSpecsResponse.builder()
                .reservationSpecId(reservationSpec.getId())
                .imgUrl(equipment.getImgUrl())
                .category(equipment.getCategory().name())
                .modelName(equipment.getModelName())
                .amount(rentalSpecByStartDateResponses.size())
                .rentalSpecs(rentalSpecByStartDateResponses)
                .build();
    }

    private static List<OverdueRentalSpecResponse> mapToRentalSpecDto(final List<RentalSpec> rentalSpecs) {
        return rentalSpecs.stream()
                .map(OverdueRentalSpecResponse::from)
                .toList();
    }

}
