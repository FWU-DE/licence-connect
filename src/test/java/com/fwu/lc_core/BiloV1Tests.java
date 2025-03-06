package com.fwu.lc_core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fwu.lc_core.shared.Bundesland;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.AvailableLicenceHolders;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.ClientLicenceHolderMappingRepository;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.ClientLicenseHolderFilterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.EnumSet;
import java.util.stream.Stream;

import static com.fwu.lc_core.shared.Constants.API_KEY_HEADER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BiloV1Tests {

    public static final String BILO_TEST_CLIENT_NAME = "bilo v1 test client id";

    @Autowired
    private MockMvc mockMvc;

    @Value("${vidis.api-key.unprivileged}")
    private String correctApiKey;

    @Autowired
    private ClientLicenseHolderFilterService clientLicenseHolderFilterService;
    @Autowired
    private ClientLicenceHolderMappingRepository clientLicenceHolderMappingRepository;

    @BeforeEach
    void setUp() {
        clientLicenceHolderMappingRepository.deleteAll();
        clientLicenseHolderFilterService.setAllowedLicenceHolders(BILO_TEST_CLIENT_NAME, EnumSet.of(AvailableLicenceHolders.BILO_V1));
    }

    @Test
    void requestWithoutApiKey() throws Exception {
        mockMvc.perform(get("/v1/ucs/request")).andExpect(status().isForbidden());
    }

    @Test
    void requestWithWrongApiKey() throws Exception {
        mockMvc.perform(
                get("/v1/ucs/request").header(API_KEY_HEADER, "wrong-api-key")
        ).andExpect(status().isForbidden());
    }

    @Test
    void requestWithLowercaseApiKeyHeaderName() throws Exception {
        var request = createValidUcsRequestDto();
        mockMvc.perform(
                get("/v1/ucs/request")
                        .header("x-api-key", correctApiKey)
                        .param("clientName", BILO_TEST_CLIENT_NAME)
                        .param("userId", request.userId)
                        .param("clientId", request.clientName)
                        .param("schulkennung", request.schulkennung)
                        .param("bundesland", request.bundesland.toString())
        ).andExpect(status().isOk());
    }

    @Test
    void requestWithUppercaseApiKeyHeaderName() throws Exception {
        var request = createValidUcsRequestDto();
        mockMvc.perform(
                get("/v1/ucs/request")
                        .header(API_KEY_HEADER, correctApiKey)
                        .param("clientName", BILO_TEST_CLIENT_NAME)
                        .param("userId", request.userId)
                        .param("clientId", request.clientName)
                        .param("schulkennung", request.schulkennung)
                        .param("bundesland", request.bundesland.toString())
        ).andExpect(status().isOk());
    }

    @Test
    void requestWithCorrectInfo() throws Exception {
        var request = createValidUcsRequestDto();
        var responseBody = mockMvc.perform(
                get("/v1/ucs/request")
                        .header(API_KEY_HEADER, correctApiKey)
                        .param("clientName", BILO_TEST_CLIENT_NAME)
                        .param("userId", request.userId)
                        .param("clientId", request.clientName)
                        .param("schulkennung", request.schulkennung)
                        .param("bundesland", request.bundesland.toString())
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        String cannedResponseFromOldApi = "{\"id\":\"student.2\",\"first_name\":\"student\",\"last_name\":\"2\",\"licenses\":[\"WES-VIDT-2369-P85R-KOUD\"],\"context\":{\"92490b9dc18341906b557bbd4071e1c97db9f9b65d348fafd30988b85a2f6924\":{\"school_name\":\"testfwu\",\"classes\":[{\"name\":\"1\",\"id\":\"cda6b1ddfa321de5a456c69fd5cee2cde7eeeae9b9d9ed24eb84fd88a35cfecb\",\"licenses\":[\"WES-VIDT-0346-P85R-KOUD\",\"WES-VIDT-9775-P85R-VWYX\"]}],\"roles\":[\"student\"],\"licenses\":[\"WES-VIDT-7368-P85R-KOUD\"]}}}";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode expected = objectMapper.readTree(cannedResponseFromOldApi);
        JsonNode actual = objectMapper.readTree(responseBody);
        assert actual.equals(expected) : "JSON objects are not the same";
    }

    @Test
    void requestWithCorrectInfoButUnconfiguredClient() throws Exception {
        var request = createValidUcsRequestDto();
        String clientName = "unconfigured-client";

        var responseBody = mockMvc.perform(
                get("/v1/ucs/request")
                        .header(API_KEY_HEADER, correctApiKey)
                        .param("clientName", clientName)
                        .param("userId", request.userId)
                        .param("clientId", request.clientName)
                        .param("schulkennung", request.schulkennung)
                        .param("bundesland", request.bundesland.toString())
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertThat(responseBody).isEqualTo("");
    }

    @ParameterizedTest
    @MethodSource("provideIncorrectInfo")
    void requestWithIncorrectInfo(String userId, String clientId, String schulkennung, String bundesland) throws Exception {
        mockMvc.perform(
                get("/v1/ucs/request")
                        .header(API_KEY_HEADER, correctApiKey)
                        .param("clientName", BILO_TEST_CLIENT_NAME)
                        .param("userId", userId)
                        .param("clientId", clientId)
                        .param("schulkennung", schulkennung)
                        .param("bundesland", bundesland)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void requestWithIncorrectBundesland() throws Exception {
        var request = createValidUcsRequestDto();
        String invalidBundesland = "XX";
        mockMvc.perform(
                get("/v1/ucs/request")
                        .header(API_KEY_HEADER, correctApiKey)
                        .param("clientName", BILO_TEST_CLIENT_NAME)
                        .param("userId", request.userId)
                        .param("clientId", request.clientName)
                        .param("schulkennung", request.schulkennung)
                        .param("bundesland", invalidBundesland)
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

    private static UcsRequestParams createValidUcsRequestDto() {
        return new UcsRequestParams("student.2", "test", null, Bundesland.MV);
    }

    private static class UcsRequestParams {
        public final String userId;
        public final String clientName;
        public final String schulkennung;
        public final Bundesland bundesland;

        public UcsRequestParams(String userId, String clientName, String schulkennung, Bundesland bundesland) {
            this.userId = userId;
            this.clientName = clientName;
            this.schulkennung = schulkennung;
            this.bundesland = bundesland;
        }
    }
}
