package com.girigiri.kwrental.item.repository;

import com.girigiri.kwrental.config.JpaConfig;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(JpaConfig.class)
class ItemQueryDslRepositoryCustomImplTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("대여 가능여부 업데이트")
    void updateRentalAvailable() {
        // given
        Item item = ItemFixture.create();
        itemRepository.save(item);

        // when
        itemRepository.updateRentalAvailable(item.getId(), false);
        entityManager.clear();

        // then
        assertThat(itemRepository.findById(item.getId()).get().isRentalAvailable()).isFalse();
    }

    @Test
    @DisplayName("자산 번호 수정")
    void updatePropertyNumber() {
        // given
        Item item = ItemFixture.create();
        itemRepository.save(item);

        // when
        String propertyNumber = "87654321";
        itemRepository.updatePropertyNumber(item.getId(), propertyNumber);
        entityManager.clear();

        // then
        assertThat(itemRepository.findById(item.getId()).get().getPropertyNumber()).isEqualTo(propertyNumber);
    }

    @Test
    @DisplayName("중복된 자산 번호로 수정하려면 예외")
    void updatePropertyNumber_duplicateKey() {
        // given
        String propertyNumber = "87654321";
        Item item = ItemFixture.builder().propertyNumber("12345678").build();
        Item item2 = ItemFixture.builder().propertyNumber(propertyNumber).build();
        itemRepository.save(item);
        itemRepository.save(item2);

        // when, then
        assertThatThrownBy(() -> itemRepository.updatePropertyNumber(item.getId(), propertyNumber))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}