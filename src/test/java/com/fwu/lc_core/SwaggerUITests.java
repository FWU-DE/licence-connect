package com.fwu.lc_core;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SwaggerUITests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void apiDocsArePubliclyAvailable() throws Exception {
        mockMvc.perform(get("/v3/api-docs")).andExpect(status().is2xxSuccessful());
    }

    @Test
    void swaggerUIIsPubliclyAvailable() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().is2xxSuccessful());
    }
}
