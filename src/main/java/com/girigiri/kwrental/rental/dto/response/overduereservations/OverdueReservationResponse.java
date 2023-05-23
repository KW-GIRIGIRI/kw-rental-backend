package com.girigiri.kwrental.rental.dto.response.overduereservations;

import com.girigiri.kwrental.inventory.domain.RentalDateTime;
import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Getter
public class OverdueReservationResponse {
    private Long reservationId;
    private String name;
    private String memberNumber;
    private LocalDateTime returnDate;
    private List<OverdueReservationSpecResponse> reservationSpecs;

    private OverdueReservationResponse() {
    }

    private OverdueReservationResponse(final Long reservationId, final String name, final String memberNumber, final LocalDateTime returnDate, final List<OverdueReservationSpecResponse> reservationSpecs) {
        this.reservationId = reservationId;
        this.name = name;
        this.memberNumber = memberNumber;
        this.returnDate = returnDate;
        this.reservationSpecs = reservationSpecs;
    }

    public static OverdueReservationResponse of(final EquipmentReservationWithMemberNumber equipmentReservation, final List<EquipmentRentalSpec> rentalSpecs) {
        final List<OverdueReservationSpecResponse> overdueReservationSpecResponses = mapToReservationSpecResponse(rentalSpecs, equipmentReservation);
        final RentalDateTime acceptDateTime = equipmentReservation.getAcceptDateTime();
        return new OverdueReservationResponse(equipmentReservation.getId(), equipmentReservation.getRenterName(),
                equipmentReservation.getMemberNumber(), acceptDateTime == null ? null : acceptDateTime.toLocalDateTime(), overdueReservationSpecResponses);
    }

    private static List<OverdueReservationSpecResponse> mapToReservationSpecResponse(final List<EquipmentRentalSpec> rentalSpecs, final EquipmentReservationWithMemberNumber equipmentReservation) {
        final Map<Long, List<EquipmentRentalSpec>> groupedRentalSpecsByReservationSpecId = rentalSpecs.stream()
                .collect(groupingBy(EquipmentRentalSpec::getReservationSpecId));
        return equipmentReservation.getReservationSpecs().stream()
                .filter(it -> groupedRentalSpecsByReservationSpecId.get(it.getId()) != null)
                .map(it -> OverdueReservationSpecResponse.of(it, groupedRentalSpecsByReservationSpecId.get(it.getId())))
                .toList();
    }
}
