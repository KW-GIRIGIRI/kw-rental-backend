package com.girigiri.kwrental.rental.domain;

import com.girigiri.kwrental.inventory.domain.RentalDateTime;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@DiscriminatorValue("equipment")
public class EquipmentRentalSpec extends AbstractRentalSpec {

    private String propertyNumber;

    protected EquipmentRentalSpec() {
    }

    @Builder
    private EquipmentRentalSpec(final Long id, final Long reservationSpecId, final Long reservationId, final String propertyNumber, final RentalSpecStatus status, final RentalDateTime acceptDateTime, final RentalDateTime returnDateTime) {
        super(id, reservationSpecId, reservationId, status, acceptDateTime, returnDateTime);
        validateNotNull(propertyNumber);
        this.propertyNumber = propertyNumber;
    }
}
