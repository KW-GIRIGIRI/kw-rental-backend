package com.girigiri.kwrental.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.girigiri.kwrental.item.dto.request.ItemPropertyNumberRequest;
import com.girigiri.kwrental.item.dto.request.UpdateItemRequest;
import com.girigiri.kwrental.item.dto.request.UpdateItemsRequest;
import com.girigiri.kwrental.item.service.ItemServiceImpl;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminItemController.class)
class AdminItemControllerTest {

    public static final String PREFIX = "/api/admin/items";
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemServiceImpl itemServiceImpl;

    @Test
    @DisplayName("중복된 품목 자산번호로 수정할 때 예외 처리")
    void updatePropertyNumber_duplicatedKey() throws Exception {
        // given
        given(itemServiceImpl.updatePropertyNumber(any(), any())).willThrow(DataIntegrityViolationException.class);
        String body = objectMapper.writeValueAsString(new ItemPropertyNumberRequest("12345678"));

        // when, then
        mockMvc.perform(patch(PREFIX + "/1/propertyNumber")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("기자재의 품목들 수정 요청 시 자산번호가 비어있을 때 예외 처리")
    void saveOrUpdate_emptyPropertyNumber() throws Exception {
        // given
        UpdateItemsRequest updateItemsRequest = new UpdateItemsRequest(List.of(new UpdateItemRequest(1L, "")));
        String body = objectMapper.writeValueAsString(updateItemsRequest);

        // when, then
        mockMvc.perform(patch(PREFIX + "?equipmentId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("기자재의 품목들 수정 요청 시 자산번호가 중복되도록 수정할 경우 예외 처리")
    void saveOrUpdate_duplicatePropertyNumber() throws Exception {
        // given
        UpdateItemsRequest updateItemsRequest = new UpdateItemsRequest(
                List.of(new UpdateItemRequest(1L, "1234567")));
        String body = objectMapper.writeValueAsString(updateItemsRequest);
        given(itemServiceImpl.updateRentalAvailable(any(), any())).willThrow(PersistenceException.class);

        // when, then
        mockMvc.perform(patch(PREFIX + "?equipmentId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}