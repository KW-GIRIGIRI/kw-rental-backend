package com.girigiri.kwrental.inventory.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.asset.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.config.JpaConfig;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.reservation.domain.entity.RentalAmount;
import com.girigiri.kwrental.reservation.domain.entity.RentalPeriod;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.InventoryFixture;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
@Import(JpaConfig.class)
class InventoryRepositoryCustomImplTest {

	@Autowired
	private InventoryRepository inventoryRepository;
	@Autowired
	private EquipmentRepository equipmentRepository;
	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@DisplayName("담은 기자재를 모두 삭제한다.")
	void deleteAll() {
		// given
		final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
		final Inventory inventory = InventoryFixture.create(equipment, 1L);
		equipmentRepository.save(equipment);
		inventoryRepository.save(inventory);

		// when
		final int expect = inventoryRepository.deleteAll(1L);

		// then
		assertThat(expect).isOne();
	}

	@Test
	@DisplayName("담은 기자재 중 대여 기간, 기자재 아이디, 회원 아이디가 동일한 것을 조회한다.")
	void findByPeriodAndEquipmentIdAndMemberId() {
		// given
		final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
		final LocalDate now = LocalDate.now();
		final RentalPeriod rentalPeriod = new RentalPeriod(now.minusDays(1), now);
		final Inventory inventory = inventoryRepository.save(
			InventoryFixture.builder(equipment).memberId(1L).rentalPeriod(rentalPeriod).build());

		// when
		final Optional<Inventory> result = inventoryRepository.findByPeriodAndEquipmentIdAndMemberId(rentalPeriod,
			equipment.getId(), 1L);

		// then
		assertThat(result.get()).isEqualTo(inventory);
	}

	@Test
	@DisplayName("담은 기자재의 대여 갯수를 수정한다.")
	void updateAmount() {
		// given
		final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
		final Inventory inventory = inventoryRepository.save(InventoryFixture.create(equipment, 0L));

		// when, then
		assertThatCode(() -> inventoryRepository.updateAmount(inventory.getId(), RentalAmount.ofPositive(10)))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("특정 기자재의 담은 기자재를 삭제한다.")
	void deleteByAssetId() {
		// given
		final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
		final Inventory inventory = inventoryRepository.save(InventoryFixture.create(equipment, 0L));

		// when
		inventoryRepository.deleteByEquipmentId(equipment.getId());

		// then
		entityManager.clear();
		final boolean actual = inventoryRepository.findById(inventory.getId()).isEmpty();
		assertThat(actual).isTrue();
	}
}