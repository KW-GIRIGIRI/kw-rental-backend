package com.girigiri.kwrental.inventory.controller;

import com.girigiri.kwrental.inventory.exception.InventoryNotFoundException;
import com.girigiri.kwrental.inventory.service.InventoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @MockBean
    private InventoryService inventoryService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("존재하지 않는 담음 기자재 삭제 시 예외 처리")
    void deleteById_notFound() throws Exception {
        // given
        doThrow(InventoryNotFoundException.class).when(inventoryService).deleteById(any());

        // when, then
        mockMvc.perform(delete("/api/inventories/1"))
                .andExpect(status().isNotFound());
    }
}