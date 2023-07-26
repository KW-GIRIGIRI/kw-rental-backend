package com.girigiri.kwrental.rental.dto.response;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;

public record EquipmentRentalSpecsResponse(
    List<EquipmentRentalSpecResponse> rentalSpecs) {

    @Builder
    public record EquipmentRentalSpecResponse(
        String status,
        LocalDate acceptDate,
        LocalDate returnDate,
        String name,
        String reason) {

        public static EquipmentRentalSpecResponse from(final RentalSpecWithName rentalSpecWithName) {
            return EquipmentRentalSpecResponse.builder()
                .status(rentalSpecWithName.getStatus().isAbnormalReturned() ? "불량 반납" : "정상 반납")
                .acceptDate(LocalDate.from(rentalSpecWithName.getAcceptDateTime()))
                .returnDate(LocalDate.from(rentalSpecWithName.getReturnDateTime()))
                .name(rentalSpecWithName.getName())
                .reason(rentalSpecWithName.getStatus().name())
                .build();
        }
    }

    public static EquipmentRentalSpecsResponse from(final List<RentalSpecWithName> rentalSpecWithNames) {
        final List<EquipmentRentalSpecResponse> equipmentRentalSpecRespons = rentalSpecWithNames.stream()
            .map(EquipmentRentalSpecResponse::from)
            .toList();
        return new EquipmentRentalSpecsResponse(equipmentRentalSpecRespons);
    }
}
