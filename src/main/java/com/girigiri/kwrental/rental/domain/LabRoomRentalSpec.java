package com.girigiri.kwrental.rental.domain;

import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;

@Entity
@DiscriminatorValue("lab_room")
public class LabRoomRentalSpec extends AbstractRentalSpec implements RentalSpec {

    protected LabRoomRentalSpec() {
    }

    @Builder
    private LabRoomRentalSpec(final Long id, final Long reservationSpecId, final Long reservationId,
                              final RentalSpecStatus status, final RentalDateTime acceptDateTime, final RentalDateTime returnDateTime) {
        super(id, reservationSpecId, reservationId, status, acceptDateTime, returnDateTime);
    }
}
