package com.girigiri.kwrental.equipment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

@WebMvcTest(AdminEquipmentController.class)
class AdminEquipmentControllerTest {

    private static final String PREFIX = "/api/admin/equipments";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EquipmentService equipmentService;

    @Test
    @DisplayName("Pageable의 size가 양수가 아닌 경우 10으로 변환된다.")
    void getEquipments_200_sizeNotPositive() throws Exception {
        // given
        long size = -1;
        final PageRequest expectPageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
        given(equipmentService.findEquipments(eq(expectPageRequest), any())).willReturn(Page.empty());

        // when, then
        mockMvc.perform(get(PREFIX + "?size=" + size))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Pageable의 page가 음수인 경우 0으로 변환된다.")
    void getEquipments_200_pageNotPositive() throws Exception {
        // given
        long page = -1;
        final PageRequest expectPageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
        given(equipmentService.findEquipments(eq(expectPageRequest), any())).willReturn(Page.empty());

        // when, then
        mockMvc.perform(get(PREFIX + "?page=" + page))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("검색어가 양끝단 공백제외 2글자 이내 예외처리.")
    void getEquipments_400_trimmedValueOver200() throws Exception {
        // given
        String keyword = "  h  ";

        // when, then
        mockMvc.perform(get(PREFIX + "?keyword=" + keyword))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("검색 카테고리가 잘못된 경우 예외처리.")
    void getEquipments_400_categoryNotMatch() throws Exception {
        // given
        String category = "notExistsCategory";

        // when, then
        mockMvc.perform(get(PREFIX + "?category=" + category))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("검색 정렬기준이 잘못된 경우 예외처리.")
    void getEquipments_400_sortNotMatch() throws Exception {
        // given
        String sort = "notExistsSort";
        given(equipmentService.findEquipments(any(), any())).willThrow(
                TransientDataAccessResourceException.class);

        // when, then
        mockMvc.perform(get(PREFIX + "?sort=" + sort))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
