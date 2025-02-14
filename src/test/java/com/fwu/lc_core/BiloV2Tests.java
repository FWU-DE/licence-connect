package com.fwu.lc_core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fwu.lc_core.shared.Bundesland;
import com.fwu.lc_core.bilov1.UcsRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class BiloV2Tests {

    @Autowired
    private MockMvc mockMvc;

    @Value("${vidis.api-key}")
    private String correctApiKey;

    @Value("${bilo.v2.auth.dummyUserId}")
    private String dummyUserId;

    @Test
    void requestWithoutApiKey() throws Exception {
        mockMvc.perform(post("/bilo/request/" + dummyUserId )).andExpect(status().isForbidden());
    }

    @Test
    void requestWithWrongApiKey() throws Exception {
        mockMvc.perform(
                post("/bilo/request/" + dummyUserId ).header("X-API-KEY", "wrong-api-key")
        ).andExpect(status().isForbidden());
    }

    @Test
    void requestWithLowercaseApiKeyHeaderName() throws Exception {
        var content = new ObjectMapper().writeValueAsString(createValidUcsRequestDto());

        mockMvc.perform(
                post("/bilo/request/" + dummyUserId )
                        .header("x-api-key", correctApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        ).andExpect(status().isOk());
    }

    @Test
    void requestWithUppercaseApiKeyHeaderName() throws Exception {
        var content = new ObjectMapper().writeValueAsString(createValidUcsRequestDto());

        mockMvc.perform(
                post("/bilo/request/" + dummyUserId )
                        .header("X-API-KEY", correctApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        ).andExpect(status().isOk());
    }

    @Test
    void requestWithIncorrectInfo() throws Exception {
        mockMvc.perform(
                post("/bilo/request").header("X-API-KEY", correctApiKey)
        ).andExpect(status().isNotFound());
    }

    @Test
    void requestWithCorrectInfoButWrongVerb() throws Exception {
        mockMvc.perform(
                get("/bilo/request/" + dummyUserId ).header("X-API-KEY", correctApiKey)
        ).andExpect(status().isMethodNotAllowed());
    }

    @Test
    void requestWithCorrectInfo() throws Exception {
        var responseBody = mockMvc.perform(
                post("/bilo/request/" + dummyUserId ).header("X-API-KEY", correctApiKey)
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        String cannedResponse = "{\"user\":{\"id\":\"" + dummyUserId + "\",\"first_name\":\"student\",\"last_name\":\"2\",\"user_alias\":null,\"roles\":[\"student\"],\"media\":[]},\"organizations\":[{\"id\":\"testfwu\",\"org_type\":\"school\",\"identifier\":null,\"authority\":null,\"name\":\"testfwu\",\"roles\":[\"student\"],\"media\":[],\"groups\":[{\"id\":\"1\",\"name\":\"1\",\"group_type\":\"class\",\"media\":[]}]}]}";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode expected = objectMapper.readTree(cannedResponse);
        JsonNode actual = objectMapper.readTree(responseBody);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void requestWithNonExistingUser() throws Exception {
        mockMvc.perform(
                post("/bilo/request/" + dummyUserId + "123").header("X-API-KEY", correctApiKey)
        ).andExpect(status().isNotFound());
    }

    private UcsRequestDto createValidUcsRequestDto() {
        return new UcsRequestDto(dummyUserId , "test", null, Bundesland.MV);
    }
}
