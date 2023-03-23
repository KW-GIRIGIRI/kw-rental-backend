package com.girigiri.kwrental.equipment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.girigiri.kwrental.equipment.exception.EquipmentNotFoundException;
import com.girigiri.kwrental.equipment.service.EquipmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
        given(equipmentService.findById(notExistsId)).willThrow(EquipmentNotFoundException.class);

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

    @Test
    @DisplayName("Pageable의 size가 양수가 아닌 경우 10으로 변환된다.")
    void getEquipments_200_sizeNotPositive() throws Exception {
        // given
        long size = -1;
        final PageRequest expectPageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
        given(equipmentService.findEquipmentsBy(eq(expectPageRequest), any())).willReturn(Page.empty());

        // when, then
        mockMvc.perform(get("/api/equipments?size=" + size))
                .andDo(print())
                .andExpect(status().isOk());
        verify(equipmentService).findEquipmentsBy(eq(expectPageRequest), any());
    }

    @Test
    @DisplayName("Pageable의 page가 음수인 경우 0으로 변환된다.")
    void getEquipments_200_pageNotPositive() throws Exception {
        // given
        long page = -1;
        final PageRequest expectPageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
        given(equipmentService.findEquipmentsBy(eq(expectPageRequest), any())).willReturn(Page.empty());

        // when, then
        mockMvc.perform(get("/api/equipments?page=" + page))
                .andDo(print())
                .andExpect(status().isOk());
        verify(equipmentService).findEquipmentsBy(eq(expectPageRequest), any());
    }

    @Test
    @DisplayName("검색어가 양끝단 공백제외 2글자 이내 예외처리.")
    void getEquipments_400_trimmedValueOver200() throws Exception {
        // given
        String keyword = "  h  ";

        // when, then
        mockMvc.perform(get("/api/equipments?keyword=" + keyword))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("검색 카테고리가 잘못된 경우 예외처리.")
    void getEquipments_400_categoryNotMatch() throws Exception {
        // given
        String category = "notExistsCategory";

        // when, then
        mockMvc.perform(get("/api/equipments?category=" + category))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("검색 정렬기준이 잘못된 경우 예외처리.")
    void getEquipments_400_sortNotMatch() throws Exception {
        // given
        String sort = "notExistsSort";
        given(equipmentService.findEquipmentsBy(any(), any())).willThrow(TransientDataAccessResourceException.class);

        // when, then
        mockMvc.perform(get("/api/equipments?sort=" + sort))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
