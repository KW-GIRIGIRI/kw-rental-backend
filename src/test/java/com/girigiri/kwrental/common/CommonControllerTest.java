package com.girigiri.kwrental.common;

import com.girigiri.kwrental.testsupport.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommonControllerTest extends ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("기본 경로로 헬스 체크를 한다.")
    void responseHealthCheck() throws Exception {
        // given, when, then
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
