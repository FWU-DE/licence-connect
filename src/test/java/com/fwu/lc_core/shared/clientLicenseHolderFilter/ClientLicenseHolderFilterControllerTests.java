package com.fwu.lc_core.shared.clientLicenseHolderFilter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.fwu.lc_core.shared.Constants.API_KEY_HEADER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
class ClientLicenseHolderFilterControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Value("${vidis.api-key.unprivileged}")
    private String unprivilegedApiKey;

    @Value("${vidis.api-key.admin}")
    private String adminApiKey;
    @Autowired
    private ClientLicenceHolderMappingRepository clientLicenceHolderMappingRepository;

    @BeforeEach
    void setUp() {
        clientLicenceHolderMappingRepository.deleteAll();
    }

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

    @Test
    void getLicenceHolders_Logs_Request(CapturedOutput output) throws Exception {
        mockMvc.perform(
                get("/admin/client-licence-holder-mapping/non-existing").header(API_KEY_HEADER, adminApiKey)
        ).andExpect(status().is2xxSuccessful());

        assertThat(output.getOut()).contains("Received request to get licence holders for client: non-existing");
    }

    @Test
    void setLicenceHolders_Logs_new_allowed_systems(CapturedOutput output) throws Exception {
        ClientLicenceHolderMappingDto expected = new ClientLicenceHolderMappingDto(EnumSet.of(AvailableLicenceHolders.BILO_V1, AvailableLicenceHolders.BILO_V2));
        String clientName = "dummy-client-name";
        String expectedFirstLog = "Setting new available licence holders for client: " + clientName;
        String expectedSecondLog = "New allowed systems: " + expected.availableLicenceHolders + " for client: " + clientName;

        mockMvc.perform(
                put("/admin/client-licence-holder-mapping/" + clientName)
                        .header(API_KEY_HEADER, adminApiKey)
                        .content(new ObjectMapper().writeValueAsString(expected))
                        .contentType("application/json")
        ).andExpect(status().is2xxSuccessful());

        String logs = output.getOut();
        assertThat(logs).contains(expectedFirstLog);
        assertThat(logs).contains(expectedSecondLog);
        assertThatFirstLogComesBeforeSecondLog(logs, expectedFirstLog, expectedSecondLog);
        assertThatBothLogsHaveTheSameTraceId(logs, expectedFirstLog, expectedSecondLog);
    }

    @Test
    void addLicenceHoldersInSeparatePuts() throws Exception {
        String clientName = "dummy-client-name";
        ClientLicenceHolderMappingDto initialMapping = new ClientLicenceHolderMappingDto(EnumSet.of(AvailableLicenceHolders.BILO_V1));
        ClientLicenceHolderMappingDto updatedMapping = new ClientLicenceHolderMappingDto(EnumSet.of(AvailableLicenceHolders.BILO_V1, AvailableLicenceHolders.BILO_V2));

        // Initial PUT request to add BILO_V1
        mockMvc.perform(
                put("/admin/client-licence-holder-mapping/" + clientName)
                        .header(API_KEY_HEADER, adminApiKey)
                        .content(new ObjectMapper().writeValueAsString(initialMapping))
                        .contentType("application/json")
        ).andExpect(status().is2xxSuccessful());

        // Verify initial mapping
        var result = mockMvc.perform(
                        get("/admin/client-licence-holder-mapping/" + clientName).header(API_KEY_HEADER, adminApiKey))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        EnumSet<AvailableLicenceHolders> actual = new ObjectMapper().readValue(result.getResponse().getContentAsString(), new TypeReference<EnumSet<AvailableLicenceHolders>>() {});
        assertThat(actual).isEqualTo(initialMapping.availableLicenceHolders);

        // Second PUT request to add BILO_V2
        mockMvc.perform(
                put("/admin/client-licence-holder-mapping/" + clientName)
                        .header(API_KEY_HEADER, adminApiKey)
                        .content(new ObjectMapper().writeValueAsString(updatedMapping))
                        .contentType("application/json")
        ).andExpect(status().is2xxSuccessful());

        // Verify updated mapping
        result = mockMvc.perform(
                        get("/admin/client-licence-holder-mapping/" + clientName).header(API_KEY_HEADER, adminApiKey))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        actual = new ObjectMapper().readValue(result.getResponse().getContentAsString(), new TypeReference<EnumSet<AvailableLicenceHolders>>() {});
        assertThat(actual).isEqualTo(updatedMapping.availableLicenceHolders);
    }

    private String findLineContaining(String text, String searchString) {
        return text.lines()
                .filter(line -> line.contains(searchString))
                .findFirst()
                .orElse("");
    }

    private String extractTraceId(String logLine) {
        Pattern pattern = Pattern.compile("traceID=([^,]+)");
        Matcher matcher = pattern.matcher(logLine);
        return matcher.find() ? matcher.group(1) : "";
    }

    private void assertThatBothLogsHaveTheSameTraceId(String logs, String expectedFirstLog, String expectedSecondLog) {
        String firstLogLine = findLineContaining(logs, expectedFirstLog);
        String secondLogLine = findLineContaining(logs, expectedSecondLog);
        String traceId1 = extractTraceId(firstLogLine);
        String traceId2 = extractTraceId(secondLogLine);
        assertThat(traceId1).isNotEmpty();
        assertThat(traceId1).isEqualTo(traceId2);
    }

    private static void assertThatFirstLogComesBeforeSecondLog(String logs, String expectedFirstLog, String expectedSecondLog) {
        assertThat(logs.indexOf(expectedFirstLog)).isLessThan(logs.indexOf(expectedSecondLog));
    }
}
