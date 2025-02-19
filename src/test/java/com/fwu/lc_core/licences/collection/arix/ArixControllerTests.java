package com.fwu.lc_core.licences.collection.arix;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ArixControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void requestWithoutApiKey() throws Exception {
        mockMvc.perform(get("/arix/request")).andExpect(status().isForbidden());
    }

    @Test
    void requestWithWrongApiKey() throws Exception {
        mockMvc.perform(
                get("/arix/request").header("X-API-KEY", "wrong-api-key")
        ).andExpect(status().isForbidden());
    }

    @Test
    void requestWithLowercaseApiKeyHeaderName() throws Exception {

        mockMvc.perform(
                post("/arix/request" + dummyUserId )
                        .header("x-api-key", correctApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        ).andExpect(status().isOk());
    }
}