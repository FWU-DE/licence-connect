package com.fwu.lc_core.licences;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LicencesTests {

    @Autowired
    private MockMvc mockMvc;

    @Value("${vidis.api-key}")
    private String correctApiKey;

    @Test
    void Unauthenticated_Request_Returns_Forbidden() throws Exception {
        mockMvc.perform(post("/v1/licences/request")).andExpect(status().isForbidden());
    }

    @Test
    void Authenticated_Request_Returns_Ok() throws Exception {
        mockMvc.perform(
                post("/v1/licences/request").header("X-API-KEY", correctApiKey)
        ).andExpect(status().isOk());
    }
}
