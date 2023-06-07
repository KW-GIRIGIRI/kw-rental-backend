package com.girigiri.kwrental.asset.labroom.domain;

import com.girigiri.kwrental.asset.domain.RentableAsset;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
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

	@Lob
	private String notice;

	protected LabRoom() {
	}

	@Builder
	private LabRoom(final Long id, final String name, final Integer totalQuantity, final Integer rentableQuantity,
		final Integer maxRentalDays,
		final boolean isAvailable, final Integer reservationCountPerDay, final String notice) {
		super(id, name, totalQuantity, rentableQuantity, maxRentalDays);
		validateNotNull(isAvailable, reservationCountPerDay);
		this.isAvailable = isAvailable;
		this.reservationCountPerDay = reservationCountPerDay;
		this.notice = notice;
	}
}
