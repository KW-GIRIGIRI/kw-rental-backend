package com.girigiri.kwrental.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.girigiri.kwrental.item.dto.request.ItemPropertyNumberRequest;
import com.girigiri.kwrental.item.service.ItemServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
}