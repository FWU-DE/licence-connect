package com.fwu.lc_core.shared.clientLicenseHolderFilter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.EnumSet;

import static com.fwu.lc_core.shared.Constants.API_KEY_HEADER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ClientLicenseHolderFilterControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Value("${vidis.api-key.unprivileged}")
    private String unprivilegedApiKey;

    @Value("${vidis.api-key.admin}")
    private String adminApiKey;

    @Test
    void Unauthenticated_Request_Returns_Forbidden() throws Exception {
        mockMvc.perform(post("/admin/client-licence-holder-mapping")).andExpect(status().isForbidden());
    }

    @Test
    void Authenticated_Request_With_Unprivileged_ApiKey_Returns_403() throws Exception {
        mockMvc.perform(
                get("/admin/client-licence-holder-mapping/non-existing").header(API_KEY_HEADER, unprivilegedApiKey)
        ).andExpect(status().isForbidden());
    }

    @Test
    void Authenticated_Request_With_Admin_ApiKey_Returns_2xx() throws Exception {
        mockMvc.perform(
                get("/admin/client-licence-holder-mapping/non-existing").header(API_KEY_HEADER, adminApiKey)
        ).andExpect(status().is2xxSuccessful());
    }

    @Test
    void Authenticated_Request_With_Admin_ApiKey_Returns_Empty_List_For_Unknown_Client() throws Exception {
        var result = mockMvc.perform(
                        get("/admin/client-licence-holder-mapping/non-existing").header(API_KEY_HEADER, adminApiKey)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("[]");
    }

    @Test
    void Admin_Request_Returns_400_When_Not_Matching_Enum() throws Exception {
        String wrongRequestBody = "{\"availableLicenceHolders\":[\"NOT_MATCHING\"]}";
        mockMvc.perform(
                put("/admin/client-licence-holder-mapping/dummy-client-name")
                        .header(API_KEY_HEADER, adminApiKey)
                        .content(wrongRequestBody)
                        .contentType("application/json")
        ).andExpect(status().isBadRequest());
    }

    @Test
    void Admin_Request_Returns_2xx_When_Matching_Enum() throws Exception {
        String content = new ObjectMapper().writeValueAsString(new ClientLicenceHolderMappingDto(EnumSet.of(AvailableLicenceHolders.BILO_V1)));
        mockMvc.perform(
                put("/admin/client-licence-holder-mapping/dummy-client-name")
                        .header(API_KEY_HEADER, adminApiKey)
                        .content(content)
                        .contentType("application/json")
        ).andExpect(status().is2xxSuccessful());
    }

    @Test
    void Authenticated_Request_With_Admin_ApiKey_Returns_Mapping_For_Existing_Client() throws Exception {
        ClientLicenceHolderMappingDto expected = new ClientLicenceHolderMappingDto(EnumSet.of(AvailableLicenceHolders.BILO_V1, AvailableLicenceHolders.BILO_V2));
        String clientName = "dummy-client-name";
        mockMvc.perform(
                put("/admin/client-licence-holder-mapping/" + clientName)
                        .header(API_KEY_HEADER, adminApiKey)
                        .content(new ObjectMapper().writeValueAsString(expected))
                        .contentType("application/json")
        ).andExpect(status().is2xxSuccessful());

        var result = mockMvc.perform(
                        get("/admin/client-licence-holder-mapping/" + clientName).header(API_KEY_HEADER, adminApiKey))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        EnumSet<AvailableLicenceHolders> actual = new ObjectMapper().readValue(contentAsString, new TypeReference<EnumSet<AvailableLicenceHolders>>() {
        });
        assertThat(actual).isEqualTo(expected.availableLicenceHolders);
    }
}
