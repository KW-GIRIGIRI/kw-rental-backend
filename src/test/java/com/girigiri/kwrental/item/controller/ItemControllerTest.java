package com.girigiri.kwrental.item.controller;

import com.girigiri.kwrental.equipment.exception.EquipmentNotFoundException;
import com.girigiri.kwrental.item.exception.ItemNotFoundException;
import com.girigiri.kwrental.item.service.ItemServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    public static final String PREFIX = "/api/items";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ItemServiceImpl itemServiceImpl;

    @Test
    @DisplayName("존재하지 않는 기자재 품목 목록 조회 예외 처리")
    void getItems_notFound() throws Exception {
        long notExistsId = 1L;
        given(itemServiceImpl.getItems(notExistsId)).willThrow(EquipmentNotFoundException.class);

        // when, then
        mockMvc.perform(get(PREFIX + "?equipmentId=" + notExistsId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("존재하지 않는 품목 조회 예외 처리")
    void getItem_notFound() throws Exception {
        // given
        long notExistsId = 1L;
        given(itemServiceImpl.getItem(notExistsId)).willThrow(ItemNotFoundException.class);

        // when, then
        mockMvc.perform(get(PREFIX + "/" + notExistsId))
                .andExpect(status().isNotFound());
    }
}