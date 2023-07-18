package com.girigiri.kwrental.rental.dto.response.overduereservations;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.girigiri.kwrental.rental.domain.EquipmentRentalSpec;
import com.girigiri.kwrental.reservation.domain.EquipmentReservationWithMemberNumber;
import com.girigiri.kwrental.reservation.domain.RentalDateTime;

import lombok.Getter;

@Getter
public class OverdueReservationResponse {
    private Long reservationId;
    private String name;
    private String memberNumber;
    private LocalDateTime overdueAcceptDateTime;
    private List<OverdueReservationSpecResponse> reservationSpecs;

    private OverdueReservationResponse() {
    }

    private OverdueReservationResponse(final Long reservationId, final String name, final String memberNumber, final LocalDateTime overdueAcceptDateTime, final List<OverdueReservationSpecResponse> reservationSpecs) {
        this.reservationId = reservationId;
        this.name = name;
        this.memberNumber = memberNumber;
        this.overdueAcceptDateTime = overdueAcceptDateTime;
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
