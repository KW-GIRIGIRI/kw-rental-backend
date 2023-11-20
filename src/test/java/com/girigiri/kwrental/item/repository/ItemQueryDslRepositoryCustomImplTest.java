package com.girigiri.kwrental.item.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.girigiri.kwrental.asset.equipment.domain.Category;
import com.girigiri.kwrental.asset.equipment.domain.Equipment;
import com.girigiri.kwrental.asset.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.dto.response.EquipmentItemDto;
import com.girigiri.kwrental.testsupport.RepositoryTest;
import com.girigiri.kwrental.testsupport.fixture.EquipmentFixture;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;

import jakarta.persistence.EntityManager;

@RepositoryTest
class ItemQueryDslRepositoryCustomImplTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private EquipmentRepository equipmentRepository;

    @Test
    @DisplayName("대여 가능 갯수를 구한다")
    void countAvailable() {
        // given
        Item item1 = ItemFixture.builder().propertyNumber("12345678").available(true).build();
        Item item2 = ItemFixture.builder().propertyNumber("87654321").available(false).build();
        itemRepository.save(item1);
        itemRepository.save(item2);

        // when
        final int expect = itemRepository.countAvailable(item1.getAssetId());

        // then
        assertThat(expect).isOne();
    }

    @Test
    @DisplayName("품목을 여러개 삭제한다.")
    void deleteByIds() {
        // given
        Item item1 = ItemFixture.builder().propertyNumber("12345678").available(true).build();
        Item item2 = ItemFixture.builder().propertyNumber("87654321").available(false).build();
        itemRepository.save(item1);
        itemRepository.save(item2);

        // when
        final long actual = itemRepository.deleteByPropertyNumbers(List.of(item1.getPropertyNumber(), item2.getPropertyNumber()));

        // then
        assertThat(actual).isEqualTo(2L);
    }

    @Test
    @DisplayName("품목을 기자재 카테고리와 모델 이름, 품목 자산번호를 페이지로 조회한다.")
    void findEquipmentItem() {
        // given
        final Equipment equipment = equipmentRepository.save(EquipmentFixture.create());
        final Item item1 = ItemFixture.builder().propertyNumber("11111111").assetId(equipment.getId()).build();
        final Item item2 = ItemFixture.builder().propertyNumber("22222222").assetId(equipment.getId()).build();
        itemRepository.saveAll(List.of(item1, item2));

        // when
        final Category category = equipment.getCategory();
        final Page<EquipmentItemDto> equipmentItem = itemRepository.findEquipmentItem(PageRequest.of(0, 2), category);

        // then
        final String modelName = equipment.getName();
        assertAll(
            () -> assertThat(equipmentItem.getTotalElements()).isEqualTo(2L),
            () -> assertThat(equipmentItem.getContent()).usingRecursiveFieldByFieldElementComparator()
                .containsExactly(new EquipmentItemDto(modelName, category, item1.getPropertyNumber()),
                    new EquipmentItemDto(modelName, category, item2.getPropertyNumber()))
        );
    }

    @Test
    @DisplayName("여러 품목을 비활성화한다.")
    void updateRentalAvailable_ids() {
        // given
        Item item1 = itemRepository.save(ItemFixture.builder().propertyNumber("11111111").build());
        Item item2 = itemRepository.save(ItemFixture.builder().propertyNumber("22222222").build());
        Item item3 = itemRepository.save(ItemFixture.builder().propertyNumber("33333333").available(false).build());

        // when
        int actual = itemRepository.updateAvailable(List.of(item1.getId(), item2.getId(), item3.getId()), false);

        // then
        assertThat(actual).isEqualTo(3);
    }

    @Test
    @DisplayName("자산번호로 삭제되지 않은 품목을 조회한다.")
    void findByPropertyNumbers() {
        // given
        Item item1 = itemRepository.save(ItemFixture.builder().propertyNumber("11111111").build());
        Item item2 = itemRepository.save(ItemFixture.builder().propertyNumber("22222222").build());
        Item deletedItem = itemRepository.save(ItemFixture.builder().propertyNumber("33333333").deletedAt(LocalDate.now()).build());

        // when
        final List<Item> actual = itemRepository.findByPropertyNumbers(List.of(item1.getPropertyNumber(), item2.getPropertyNumber(), deletedItem.getPropertyNumber()));

        // then
        assertThat(actual).containsExactlyInAnyOrder(item1, item2);
    }
}