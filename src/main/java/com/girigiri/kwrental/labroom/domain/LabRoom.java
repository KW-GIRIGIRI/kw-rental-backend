package com.girigiri.kwrental.labroom.domain;

import com.girigiri.kwrental.asset.domain.RentableAsset;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DiscriminatorValue("lab_room")
public class LabRoom extends RentableAsset {

	private boolean isAvailable;
	private Integer reservationCountPerDay;

	protected LabRoom() {
	}

	@Builder
	private LabRoom(final Long id, final String name, final Integer totalQuantity, final Integer maxRentalDays,
		final boolean isAvailable, final Integer reservationCountPerDay) {
		super(id, name, totalQuantity, maxRentalDays);
		validateNotNull(isAvailable, reservationCountPerDay);
		this.isAvailable = isAvailable;
		this.reservationCountPerDay = reservationCountPerDay;
	}
}
