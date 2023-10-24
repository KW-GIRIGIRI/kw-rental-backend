package com.girigiri.kwrental.inventory.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.inventory.domain.Inventory;

import lombok.Builder;

@Builder
public record InventoriesResponse(

	List<InventoryResponse> inventories) {

	public static InventoriesResponse from(final List<Inventory> inventories) {
		final List<InventoryResponse> inventoryResponses = inventories.stream()
			.map(InventoryResponse::from)
			.toList();
		return new InventoriesResponse(inventoryResponses);
	}

	@Builder
	public record InventoryResponse(
		Long id, String rentalPlace, String modelName, String category, String maker, String imgUrl,
		LocalDate rentalStartDate, LocalDate rentalEndDate, Integer amount, Long equipmentId) {

		public static InventoryResponse from(final Inventory inventory) {
			final Equipment equipment = inventory.getAsset().as(Equipment.class);
			return InventoryResponse.builder()
				.id(inventory.getId())
				.rentalPlace(equipment.getRentalPlace())
				.modelName(equipment.getName())
				.category(equipment.getCategory().name())
				.maker(equipment.getMaker())
				.equipmentId(equipment.getId())
				.imgUrl(equipment.getImgUrl())
				.rentalStartDate(inventory.getRentalPeriod().getRentalStartDate())
				.rentalEndDate(inventory.getRentalPeriod().getRentalEndDate())
				.amount(inventory.getRentalAmount().getAmount())
				.build();
		}
	}
}
