package com.fwu.lc_core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fwu.lc_core.bilov1.UcsRequestDto;
import com.fwu.lc_core.bilov1.Bundesland;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BiloV1Tests {

    @Autowired
    private MockMvc mockMvc;

    @Value("${vidis.api-key}")
    private String correctApiKey;

    @Test
    void requestWithoutApiKey() throws Exception {
        mockMvc.perform(post("/v1/ucs/request")).andExpect(status().isUnauthorized());
    }

    @Test
    void requestWithWrongApiKey() throws Exception {
        mockMvc.perform(
                post("/v1/ucs/request").header("X-API-KEY", "wrong-api-key")
        ).andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("provideIncorrectInfo")
    void requestWithIncorrectInfo(String userId, String clientId, String schulkennung, String bundesland) throws Exception {
        UcsRequestDto request = buildRequestForParametrizedTests(userId, clientId, schulkennung, bundesland);

        mockMvc.perform(
                post("/v1/ucs/request")
                        .header("X-API-KEY", correctApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void requestWithCorrectInfo() throws Exception {
        UcsRequestDto request = new UcsRequestDto(
                "userId", "clientId", "schulkennung", Bundesland.BY
        );
        mockMvc.perform(
                post("/v1/ucs/request")
                        .header("X-API-KEY", correctApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
        ).andExpect(status().isOk());
    }


    private static Stream<Arguments> provideIncorrectInfo() {
        return Stream.of(
                Arguments.of(null, "clientId", "schulkennung", "BY"),
                Arguments.of("userId", null, "schulkennung", "BY"),
                Arguments.of("userId", "clientId", "schulkennung", null),
                Arguments.of("", "clientId", "schulkennung", "BY"),
                Arguments.of("userId", "", "schulkennung", "BY"),
                Arguments.of("userId", "clientId", "schulkennung", "")
        );
    }

    private static UcsRequestDto buildRequestForParametrizedTests(String userId, String clientId, String schulkennung, String bundesland) {
        UcsRequestDto request = new UcsRequestDto(
                userId, clientId, schulkennung, (bundesland == null || bundesland.isEmpty()) ? null : Bundesland.valueOf(bundesland)
        );
        return request;
    }
}
