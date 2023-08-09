package com.girigiri.kwrental.rental.domain.entity;

import com.girigiri.kwrental.rental.domain.RentalSpecStatus;
import com.girigiri.kwrental.reservation.domain.entity.RentalDateTime;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@DiscriminatorValue("equipment")
public class EquipmentRentalSpec extends RentalSpec {

	private String propertyNumber;

	protected EquipmentRentalSpec() {
	}

	@Builder
	private EquipmentRentalSpec(final Long id, final Long reservationSpecId, final Long reservationId,
		final String propertyNumber, final RentalSpecStatus status, final RentalDateTime acceptDateTime,
		final RentalDateTime returnDateTime) {
		super(id, reservationSpecId, reservationId, status, acceptDateTime, returnDateTime);
		validateNotNull(propertyNumber);
        this.propertyNumber = propertyNumber;
    }
}
