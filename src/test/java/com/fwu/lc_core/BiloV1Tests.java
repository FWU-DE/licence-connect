package com.fwu.lc_core;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BiloV1Tests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void Unauthenticated_Request_Returns_Forbidden() throws Exception {
        mockMvc.perform(post("/v1/ucs/request")).andExpect(status().isForbidden());
    }
}
