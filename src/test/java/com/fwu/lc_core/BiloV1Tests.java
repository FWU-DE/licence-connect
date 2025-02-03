package com.fwu.lc_core;

import com.fasterxml.jackson.databind.JsonNode;
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

    @Test
    void requestWithCorrectInfo() throws Exception {
        String studentId = "student.2";
        UcsRequestDto request = new UcsRequestDto(
                studentId, "test", null, Bundesland.MV
        );

        var response = mockMvc.perform(
                post("/v1/ucs/request")
                        .header("X-API-KEY", correctApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
        ).andExpect(status().isOk()).andReturn();

        String responseBody = response.getResponse().getContentAsString();
        String json1 = "{\"id\":\"student.2\",\"first_name\":\"student\",\"last_name\":\"2\",\"licenses\":[\"WES-VIDT-2369-P85R-KOUD\"],\"context\":{\"92490b9dc18341906b557bbd4071e1c97db9f9b65d348fafd30988b85a2f6924\":{\"school_name\":\"testfwu\",\"classes\":[{\"name\":\"1\",\"id\":\"cda6b1ddfa321de5a456c69fd5cee2cde7eeeae9b9d9ed24eb84fd88a35cfecb\",\"licenses\":[\"WES-VIDT-0346-P85R-KOUD\",\"WES-VIDT-9775-P85R-VWYX\"]}],\"roles\":[\"student\"],\"licenses\":[\"WES-VIDT-7368-P85R-KOUD\"]}}}";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode tree1 = objectMapper.readTree(json1);
        JsonNode tree2 = objectMapper.readTree(responseBody);
        assert tree2.equals(tree1) : "JSON objects are not the same";
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
