package com.girigiri.kwrental.item.domain;

import java.time.LocalDate;

import com.girigiri.kwrental.item.exception.ItemException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class Item {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long id;

	@Column(nullable = false)
	private String propertyNumber;

	private boolean available = true;

	@Column(nullable = false)
	private Long assetId;

	private LocalDate deletedAt;

	protected Item() {
	}

	@Builder
	private Item(final Long id, final String propertyNumber, final boolean available, final Long assetId,
		final LocalDate deletedAt) {
		if (propertyNumber == null || propertyNumber.trim().isEmpty()) {
			throw new ItemException("자산 번호가 잘못됐습니다.");
		}
		if (assetId == null)
			throw new ItemException("품목이 기자재에 소속되지 않은 것 같습니다.");
		this.id = id;
		this.propertyNumber = propertyNumber.trim();
		this.available = available;
		this.assetId = assetId;
		this.deletedAt = deletedAt;
	}

	public void setAvailable(final boolean available) {
		this.available = available;
	}

	public void setPropertyNumber(final String propertyNumber) {
		this.propertyNumber = propertyNumber;
	}

	public boolean canUpdatePropertyNumberTo(final String propertyNumber) {
		if (propertyNumber == null || propertyNumber.isBlank() || propertyNumber.trim().isEmpty())
			return false;
		return !propertyNumber.equals(this.propertyNumber);
	}
}
