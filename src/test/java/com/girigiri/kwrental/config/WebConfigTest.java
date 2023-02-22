package com.girigiri.kwrental.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class WebConfigTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("URI가 /api/**인 요청에 CORS를 허용한다.")
    void allowCors() throws Exception {
        // given, when
        mockMvc.perform(options("/api")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Origin", "http://some-origin:1234"))

                //then
                .andExpect(status().is2xxSuccessful())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"))
                .andExpect(header().string("Access-Control-Allow-Methods",
                        "GET,OPTIONS,POST,DELETE,PUT,HEAD,PATCH,TRACE"))
                .andDo(print());
    }
}
