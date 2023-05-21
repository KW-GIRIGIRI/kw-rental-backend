package com.girigiri.kwrental.rental.dto.response.overduereservations;

import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.rental.domain.RentalSpec;
import com.girigiri.kwrental.reservation.domain.ReservationSpec;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class OverdueReservationSpecResponse {

    private Long reservationSpecId;
    private Long equipmentId;
    private String imgUrl;
    private String category;
    private String modelName;
    private Integer amount;
    private List<OverdueRentalSpecResponse> rentalSpecs;

    private OverdueReservationSpecResponse() {
    }

    @Builder
    private OverdueReservationSpecResponse(final Long reservationSpecId, final Long equipmentId, final String imgUrl, final String category, final String modelName, final Integer amount, final List<OverdueRentalSpecResponse> rentalSpecs) {
        this.reservationSpecId = reservationSpecId;
        this.equipmentId = equipmentId;
        this.imgUrl = imgUrl;
        this.category = category;
        this.modelName = modelName;
        this.amount = amount;
        this.rentalSpecs = rentalSpecs;
    }

    public static OverdueReservationSpecResponse of(final ReservationSpec reservationSpec, final List<RentalSpec> rentalSpecs) {
        final Equipment equipment = reservationSpec.getRentable().as(Equipment.class);
        final List<OverdueRentalSpecResponse> rentalSpecByStartDateResponses = mapToRentalSpecDto(rentalSpecs);
        return OverdueReservationSpecResponse.builder()
                .reservationSpecId(reservationSpec.getId())
                .equipmentId(equipment.getId())
                .imgUrl(equipment.getImgUrl())
                .category(equipment.getCategory().name())
                .modelName(equipment.getName())
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
