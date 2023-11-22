package com.girigiri.kwrental.asset.domain;

import java.time.LocalDate;

import com.girigiri.kwrental.asset.exception.RentableAssetException;
import com.girigiri.kwrental.common.AbstractSuperEntity;
import com.girigiri.kwrental.reservation.exception.NotEnoughAmountException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "asset")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class RentableAsset extends AbstractSuperEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Integer totalQuantity;

	@Column(nullable = false)
	private Integer rentableQuantity;

	@Column(nullable = false)
	private Integer maxRentalDays;
	private LocalDate deletedAt;

	protected RentableAsset() {
	}

	private RentableAsset(final Long id, final String name, final Integer totalQuantity,
		final Integer rentableQuantity, final Integer maxRentalDays, final LocalDate deletedAt) {
		if (totalQuantity < rentableQuantity) {
			throw new RentableAssetException("전체 갯수가 대여 가능 갯수보다 작으면 안됩니다.");
		}
		if (totalQuantity < 0) {
			throw new RentableAssetException("전체 갯수는 0보다 커야 합니다.");
		}
		if (rentableQuantity < 0) {
			throw new RentableAssetException("대여 가능 갯수는 0보다 커야 합니다.");
		}
		this.id = id;
		this.name = name;
		this.rentableQuantity = rentableQuantity;
		this.totalQuantity = totalQuantity;
		this.maxRentalDays = maxRentalDays;
		this.deletedAt = deletedAt;
	}

	protected RentableAsset(Long id, String name, Integer totalQuantity, Integer rentableQuantity,
		Integer maxRentalDays) {
		this(id, name, totalQuantity, rentableQuantity, maxRentalDays, null);
	}

	public boolean canRentDaysFor(final Integer rentalDays) {
		return this.maxRentalDays.compareTo(rentalDays) >= 0;
	}

	public void validateAmountForRent(int amount) {
		if (amount > rentableQuantity) {
			throw new NotEnoughAmountException();
		}
	}

	public boolean isDeleted() {
		return this.deletedAt != null;
	}

	public void delete() {
		this.deletedAt = LocalDate.now();
	}

	public abstract int getRemainQuantity(int reservedCount);
}
