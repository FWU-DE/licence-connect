package com.fwu.lc_core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fwu.lc_core.shared.Bundesland;
import com.fwu.lc_core.bilov1.UcsRequestDto;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.AvailableLicenceHolders;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.ClientLicenceHolderMappingRepository;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.ClientLicenseHolderFilterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.EnumSet;

import static com.fwu.lc_core.shared.Constants.API_KEY_HEADER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class BiloV2Tests {

    public static final String BILO_TEST_CLIENT_NAME = "bilo v2 test client id";
    @Autowired
    private MockMvc mockMvc;

    @Value("${vidis.api-key.unprivileged}")
    private String correctApiKey;

    @Value("${bilo.v2.auth.dummyUserId}")
    private String dummyUserId;

    @Autowired
    private ClientLicenseHolderFilterService clientLicenseHolderFilterService;
    @Autowired
    private ClientLicenceHolderMappingRepository clientLicenceHolderMappingRepository;

    @BeforeEach
    void setUp() {
        clientLicenceHolderMappingRepository.deleteAll();
        clientLicenseHolderFilterService.setAllowedLicenceHolders(BILO_TEST_CLIENT_NAME, EnumSet.of(AvailableLicenceHolders.BILO_V2));
    }

    @Test
    void requestWithoutApiKey() throws Exception {
        mockMvc.perform(post("/bilo/request/" + dummyUserId)).andExpect(status().isForbidden());
    }

    @Test
    void requestWithWrongApiKey() throws Exception {
        mockMvc.perform(
                post("/bilo/request/" + dummyUserId)
                        .header(API_KEY_HEADER, "wrong-api-key")
        ).andExpect(status().isForbidden());
    }

    @Test
    void requestWithLowercaseApiKeyHeaderName() throws Exception {
        var content = new ObjectMapper().writeValueAsString(createValidUcsRequestDto());

        mockMvc.perform(
                post("/bilo/request/" + dummyUserId)
                        .param("clientName", BILO_TEST_CLIENT_NAME)
                        .header("x-api-key", correctApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        ).andExpect(status().isOk());
    }

    @Test
    void requestWithUppercaseApiKeyHeaderName() throws Exception {
        var content = new ObjectMapper().writeValueAsString(createValidUcsRequestDto());

        mockMvc.perform(
                post("/bilo/request/" + dummyUserId)
                        .param("clientName", BILO_TEST_CLIENT_NAME)

                        .header(API_KEY_HEADER, correctApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        ).andExpect(status().isOk());
    }

    @Test
    void requestWithIncorrectInfo() throws Exception {
        mockMvc.perform(
                post("/bilo/request").header(API_KEY_HEADER, correctApiKey)
        ).andExpect(status().isNotFound());
    }

    @Test
    void requestWithCorrectInfoButWrongVerb() throws Exception {
        mockMvc.perform(
                get("/bilo/request/" + dummyUserId).header(API_KEY_HEADER, correctApiKey)
        ).andExpect(status().isMethodNotAllowed());
    }

    @Test
    void requestWithCorrectInfoButUnconfiguredClient() throws Exception {
        String clientId = "unconfigured-client";

        var responseBody = mockMvc.perform(
                post("/bilo/request/" + dummyUserId)
                        .param("clientName", clientId)
                        .header(API_KEY_HEADER, correctApiKey)
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertThat(responseBody).isEqualTo("[]");
    }


    @Test
    void requestWithCorrectInfo() throws Exception {
        var responseBody = mockMvc.perform(
                post("/bilo/request/" + dummyUserId)
                        .param("clientName", BILO_TEST_CLIENT_NAME)
                        .header(API_KEY_HEADER, correctApiKey)
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
                post("/bilo/request/" + dummyUserId + "123")
                        .param("clientName", BILO_TEST_CLIENT_NAME)
                        .header(API_KEY_HEADER, correctApiKey)
        ).andExpect(status().isNotFound());
    }

    private UcsRequestDto createValidUcsRequestDto() {
        return new UcsRequestDto(dummyUserId, "test", null, Bundesland.MV);
    }
}
