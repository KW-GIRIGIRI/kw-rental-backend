package com.girigiri.kwrental.item.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Transactional;

import com.girigiri.kwrental.asset.equipment.service.EquipmentDeleteEvent;
import com.girigiri.kwrental.item.repository.ItemRepository;

@SpringBootTest
class ItemEventListenerTest {

    @Autowired
    private ApplicationEventPublisher publisher;

    @MockBean
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemEventListener itemEventListener;

    @Test
    @DisplayName("트랜잭션 상황에서 기자재 삭제 이벤트가 발생시 품목도 모두 제거된다.")
    @Transactional
    void handleEquipmentDelete() {
        // given
        EquipmentDeleteEvent equipmentDeleteEvent = new EquipmentDeleteEvent(this, 1L);

        // when
        publisher.publishEvent(equipmentDeleteEvent);

        // then
        verify(itemRepository).deleteByAssetId(1L);
    }

    @Test
    @DisplayName("트랜잭션이 아닌 상황에서 기자재 삭제 이벤트 발생 시 예외가 발생한다.")
    void handleEquipmentDelete_exceptionNonTransaction() {
        // given
        EquipmentDeleteEvent equipmentDeleteEvent = new EquipmentDeleteEvent(this, 1L);

        // when, then
        assertThatThrownBy(() -> publisher.publishEvent(equipmentDeleteEvent))
                .isExactlyInstanceOf(IllegalTransactionStateException.class);
    }
}