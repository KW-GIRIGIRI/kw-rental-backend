package com.girigiri.kwrental.asset.repository;

public interface AssetRepositoryCustom {
	void updateRentableQuantity(Long id, int rentableQuantity);

	void deleteById(Long id);
}
