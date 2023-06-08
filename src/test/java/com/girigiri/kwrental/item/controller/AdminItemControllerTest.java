package com.girigiri.kwrental.item.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.girigiri.kwrental.item.dto.request.ItemPropertyNumberRequest;
import com.girigiri.kwrental.item.dto.request.SaveOrUpdateItemsRequest;
import com.girigiri.kwrental.item.dto.request.UpdateItemRequest;
import com.girigiri.kwrental.testsupport.ControllerTest;

import jakarta.persistence.PersistenceException;

class AdminItemControllerTest extends ControllerTest {

    public static final String PREFIX = "/api/admin/items";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("중복된 품목 자산번호로 수정할 때 예외 처리")
    void updatePropertyNumber_duplicatedKey() throws Exception {
        // given
        given(itemService.updatePropertyNumber(any(), any())).willThrow(DataIntegrityViolationException.class);
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
        SaveOrUpdateItemsRequest updateItemsRequest = new SaveOrUpdateItemsRequest(List.of(new UpdateItemRequest(1L, "")));
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
        SaveOrUpdateItemsRequest updateItemsRequest = new SaveOrUpdateItemsRequest(
                List.of(new UpdateItemRequest(1L, "1234567")));
        String body = objectMapper.writeValueAsString(updateItemsRequest);
        given(itemService.updateRentalAvailable(any(), anyBoolean())).willThrow(PersistenceException.class);

        // when, then
        mockMvc.perform(patch(PREFIX + "?equipmentId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}