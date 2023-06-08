package com.girigiri.kwrental.asset.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.girigiri.kwrental.asset.domain.RentableAsset;
import com.girigiri.kwrental.config.JpaConfig;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
@Import(JpaConfig.class)
class AssetRepositoryTest {

	@Autowired
	private AssetRepository assetRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@DisplayName("대여 가능 갯수를 업데이트 한다.")
	void updateRentableQuantity() {
		// given
		RentableAsset asset = assetRepository.save(
			EquipmentFixture.builder().totalQuantity(1).rentableQuantity(1).build());

		// when
		assetRepository.updateRentableQuantity(asset.getId(), 2);

		// then
		entityManager.refresh(asset);
		assertThat(asset.getRentableQuantity()).isEqualTo(2);
	}

	@Test
	@DisplayName("자산응 삭제 처리한다.")
	void deleteById() {
		// given
		RentableAsset asset = assetRepository.save(
			EquipmentFixture.builder().totalQuantity(1).rentableQuantity(1).build());

		// when
		assetRepository.deleteById(asset.getId());

		// then
		entityManager.refresh(asset);
		assertThat(asset.getDeletedAt()).isNotNull();
	}
}