package com.fwu.lc_core.licences.collection.arix;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fwu.lc_core.shared.Bundesland;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ArixControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Value("${vidis.api-key}")
    private String correctApiKey;

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
        String content = new ObjectMapper().writeValueAsString(generateValidArixRequestDtoWithBundesland());

        mockMvc.perform(
                get("/arix/request")
                        .header("x-api-key", correctApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        ).andExpect(status().isOk());
    }

    @Test
    void requestWithUppercaseApiKeyHeaderName() throws Exception {
        String content = new ObjectMapper().writeValueAsString(generateValidArixRequestDtoWithBundesland());

        mockMvc.perform(
                get("/arix/request")
                        .header("X-API-KEY", correctApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        ).andExpect(status().isOk());
    }

    @Test
    public void RequestLicences_BundeslandOnly_Returns_StringWithLicenceId()  throws Exception  {
        String content = new ObjectMapper().writeValueAsString(generateValidArixRequestDtoWithBundesland());

        String responseBody = mockMvc.perform(
                get("/arix/request")
                        .header("X-API-KEY", correctApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertThat(responseBody).contains("BY_1_23ui4g23c");

    }

    private static ArixRequestDto generateValidArixRequestDtoWithBundesland() {
        return new ArixRequestDto(Bundesland.BY, null, null, null);
    }
}