package com.girigiri.kwrental.inventory.repository;

import com.girigiri.kwrental.config.JpaConfig;
import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.inventory.domain.Inventory;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.InventoryFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
class InventoryRepositoryCustomImplTest {

    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private EquipmentRepository equipmentRepository;

    @Test
    @DisplayName("담은 기자재를 모두 삭제한다.")
    void deleteAll() {
        // given
        final Equipment equipment = EquipmentFixture.builder().build();
        final Inventory inventory = InventoryFixture.create(equipment, 1L);
        equipmentRepository.save(equipment);
        inventoryRepository.save(inventory);

        // when
        final int expect = inventoryRepository.deleteAll(1L);

        // then
        assertThat(expect).isOne();
    }
}