package com.girigiri.kwrental.item.controller;

import com.girigiri.kwrental.equipment.exception.EquipmentNotFoundException;
import com.girigiri.kwrental.item.exception.ItemNotFoundException;
import com.girigiri.kwrental.testsupport.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ItemControllerTest extends ControllerTest {

    public static final String PREFIX = "/api/items";

    @Test
    @DisplayName("존재하지 않는 기자재 품목 목록 조회 예외 처리")
    void getItems_notFound() throws Exception {
        long notExistsId = 1L;
        given(itemService.getItems(notExistsId)).willThrow(EquipmentNotFoundException.class);

        // when, then
        mockMvc.perform(get(PREFIX + "?equipmentId=" + notExistsId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("존재하지 않는 품목 조회 예외 처리")
    void getItem_notFound() throws Exception {
        // given
        long notExistsId = 1L;
        given(itemService.getItem(notExistsId)).willThrow(ItemNotFoundException.class);

        // when, then
        mockMvc.perform(get(PREFIX + "/" + notExistsId))
                .andExpect(status().isNotFound());
    }
}