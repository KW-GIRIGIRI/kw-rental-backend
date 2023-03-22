package com.girigiri.kwrental.equipment.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.girigiri.kwrental.equipment.exception.EquipmentNotFoundException;
import com.girigiri.kwrental.equipment.service.EquipmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {EquipmentController.class})
class EquipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EquipmentService equipmentService;

    @Test
    @DisplayName("등록되지 않은 기자재 id로 기자재 상세 조회 요청하면 404를 응답한다.")
    void getEquipment_404_notFoundId() throws Exception {
        // given
        long notExistsId = 1L;
        BDDMockito.given(equipmentService.findById(notExistsId)).willThrow(EquipmentNotFoundException.class);

        // when, then
        mockMvc.perform(get("/api/equipments/" + notExistsId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("id가 문자열이면 400을 응답한다.")
    void getEquipment_400_idIsNotNumeric() throws Exception {
        // given
        String invalidId = "hi";

        // when, then
        mockMvc.perform(get("/api/equipments/" + invalidId))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
