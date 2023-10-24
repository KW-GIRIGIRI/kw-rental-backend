package com.girigiri.kwrental.asset.labroom.domain;

import com.girigiri.kwrental.asset.domain.RentableAsset;
import com.girigiri.kwrental.asset.labroom.exception.LabRoomException;

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

	@Override
	public Integer getRemainQuantity(final int reservedCount) {
		if (reservedCount > getRentableQuantity()) {
			throw new LabRoomException("대여 가능 갯수가 대여 된 갯수보다 크면 안됩니다!");
		}
		if (!this.isAvailable)
			return 0;
		return getRentableQuantity() - reservedCount;
	}

	public Integer getRemainReservationCount(final Integer reservedReservationCount) {
		if (reservedReservationCount > this.reservationCountPerDay) {
			throw new LabRoomException("남은 대여 신청 횟수가 대여 가능 신청 횟수보다 크면 안됩니다!");
		}
		if (!this.isAvailable)
			return 0;
		return this.reservationCountPerDay - reservedReservationCount;
	}
}
