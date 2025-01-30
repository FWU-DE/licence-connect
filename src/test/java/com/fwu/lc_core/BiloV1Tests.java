package com.fwu.lc_core;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BiloV1Tests {

    @Autowired
    private MockMvc mockMvc;

    @Value("${vidis.api-key}")
    private String correctApiKey;

    String jsonPayload = "{ \"student\": \"John Doe\" }";

    @Test
    void Unauthenticated_Request_Returns_Forbidden() throws Exception {
        mockMvc.perform(post("/v1/ucs/request")).andExpect(status().isForbidden());
    }

    @Test
    void Authenticated_Request_Returns_Ok() throws Exception {
        mockMvc.perform(
                post("/v1/ucs/request")
                        .content(jsonPayload)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-KEY", correctApiKey)
        ).andExpect(status().isOk());
    }

    @Test
    void sendingV1TestDataReturnsRespectiveLicenceData() throws Exception {


        mockMvc.perform(
                post("/v1/ucs/request")
                        .content(jsonPayload)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-KEY", correctApiKey)
        ).andExpect(status().isOk())
                .andExpect(result -> assertFalse(result.getResponse().getContentAsString().isEmpty()));
    }
}
