package com.girigiri.kwrental.inventory.controller;

import com.girigiri.kwrental.auth.domain.Role;
import com.girigiri.kwrental.auth.domain.SessionMember;
import com.girigiri.kwrental.inventory.exception.InventoryNotFoundException;
import com.girigiri.kwrental.testsupport.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InventoryControllerTest extends ControllerTest {

    @Test
    @DisplayName("존재하지 않는 담음 기자재 삭제 시 예외 처리")
    void deleteById_notFound() throws Exception {
        // given
        doThrow(InventoryNotFoundException.class).when(inventoryService).deleteById(any(), any());
        final MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", new SessionMember(1L, "20000101", Role.USER));

        // when, then
        mockMvc.perform(delete("/api/inventories/1")
                        .session(session))
                .andExpect(status().isNotFound());
    }
}