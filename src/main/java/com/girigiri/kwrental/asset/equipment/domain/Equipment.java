package com.girigiri.kwrental.asset.equipment.domain;

import com.girigiri.kwrental.asset.domain.RentableAsset;
import com.girigiri.kwrental.asset.equipment.exception.EquipmentException;
import com.girigiri.kwrental.asset.exception.RentableAssetException;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Setter
@DiscriminatorValue("equipment")
public class Equipment extends RentableAsset {

	@Enumerated(EnumType.STRING)
	private Category category;

	private String maker;

	private String imgUrl;

	private String description;

	private String components;

	private String purpose;

	private String rentalPlace;

	protected Equipment() {
	}

	@Builder
	public Equipment(final Long id, final Category category, final String maker, final String name,
		final String imgUrl, final String description,
		final String components, final String purpose, final String rentalPlace,
		final Integer totalQuantity, final Integer rentableQuantity, final Integer maxRentalDays) {
		super(id, name, totalQuantity, rentableQuantity, maxRentalDays);
		validateNotNull(category, maker, imgUrl, rentalPlace);
		this.category = category;
		this.maker = maker;
		this.imgUrl = imgUrl;
		this.description = description;
		this.components = components;
		this.purpose = purpose;
		this.rentalPlace = rentalPlace;
	}

	public void adjustToRentalQuantity(final int operand) {
		validateAdjustToRentableQuantity(operand);
		this.setRentableQuantity(this.getRentableQuantity() + operand);
	}

	private void validateAdjustToRentableQuantity(final int operand) {
		final int adjustedRentableQuantity = this.getRentableQuantity() + operand;
		if (adjustedRentableQuantity > this.getTotalQuantity()) {
			throw new RentableAssetException("대여 가능 갯수가 전체 갯수보다 클 수 없습니다.");
		}
		if (adjustedRentableQuantity < 0) {
			throw new RentableAssetException("대여 가능 갯수가 0보다 작을 수 없습니다.");
		}
	}

	public void reduceTotalCount(final int count) {
		if (getTotalQuantity() < count)
			throw new RentableAssetException("전체 수량을 0이하로 줄일 수 없습니다.");
		this.setTotalQuantity(this.getTotalQuantity() - count);
	}

	public void addTotalCount(final int count) {
		this.setTotalQuantity(this.getTotalQuantity() + count);
	}

	@Override
	public Integer getRemainQuantity(int reservedCount) {
		if (reservedCount > getRentableQuantity()) {
			throw new EquipmentException("대여 가능 갯수가 대여 된 갯수보다 크면 안됩니다!");
		}
		return getRentableQuantity() - reservedCount;
	}
}
