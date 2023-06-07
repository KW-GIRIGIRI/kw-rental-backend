package com.girigiri.kwrental.asset.domain;

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
public abstract class RentableAsset extends AbstractSuperEntity implements Rentable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = false)
	private Integer totalQuantity;

	@Column(nullable = false)
	private Integer rentableQuantity;

	@Column(nullable = false)
	private Integer maxRentalDays;

	protected RentableAsset() {
	}

	protected RentableAsset(final Long id, final String name, final Integer totalQuantity,
		final Integer rentableQuantity, final Integer maxRentalDays) {
		this.id = id;
		this.name = name;
		this.rentableQuantity = rentableQuantity;
		this.totalQuantity = totalQuantity;
		this.maxRentalDays = maxRentalDays;
	}

	@Override
	public boolean canRentDaysFor(final Integer rentalDays) {
		return this.maxRentalDays.compareTo(rentalDays) >= 0;
	}

	@Override
	public void validateAmountForRent(int amount) {
		if (amount > rentableQuantity) {
			throw new NotEnoughAmountException();
		}
	}
}
