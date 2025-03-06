package com.fwu.lc_core.licences;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fwu.lc_core.licences.models.Licence;
import com.fwu.lc_core.licences.models.LicencesRequestDto;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

import static com.fwu.lc_core.shared.Constants.API_KEY_HEADER;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LicencesControllerTests {

    private static final String GENERIC_LICENCES_TEST_CLIENT_NAME = "generic licences test client name";
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
        clientLicenseHolderFilterService.setAllowedLicenceHolders(GENERIC_LICENCES_TEST_CLIENT_NAME, EnumSet.of(AvailableLicenceHolders.ARIX));
    }

    @Test
    void Unauthenticated_Request_Returns_Forbidden() throws Exception {
        mockMvc.perform(get("/v1/licences/request")).andExpect(status().isForbidden());
    }

    @Test
    void Authenticated_Request_WithoutBody_Returns_BadRequest() throws Exception {
        mockMvc.perform(
                get("/v1/licences/request").header(API_KEY_HEADER, correctApiKey)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void Authenticated_Request_Without_ClientName_Parameter_Returns_BadRequest() throws Exception {
        var requestDto = new RelaxedLicencesRequestDto("ABC", null, null, null);

        mockMvc.perform(
                get("/v1/licences/request")
                        .header(API_KEY_HEADER, correctApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void Authenticated_Request_WithInvalidBundesland_Returns_BadRequest() throws Exception {
        var requestDto = new RelaxedLicencesRequestDto("ABC", null, null, null);

        mockMvc.perform(
                get("/v1/licences/request")
                        .header(API_KEY_HEADER, correctApiKey)
                        .param("clientName", GENERIC_LICENCES_TEST_CLIENT_NAME)
                        .param("bundesland", requestDto.bundesland())
                        .param("standortnummer", requestDto.standortnummer())
                        .param("schulnummer", requestDto.schulnummer())
                        .param("userId", requestDto.userId())
        ).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("provideValidInputAndOutput")
    void Authenticated_Request_WithValidBody_Returns_CorrectLicences(Bundesland bundesland, String standortnummer, String schulnummer, String userId, List<String> expectedLicenceCodes) throws Exception {
        var requestDto = new LicencesRequestDto(bundesland, standortnummer, schulnummer, userId);
        var responseBody = mockMvc.perform(
                get("/v1/licences/request")
                        .header(API_KEY_HEADER, correctApiKey)
                        .param("clientName", GENERIC_LICENCES_TEST_CLIENT_NAME)
                        .param("bundesland", requestDto.bundesland().name())
                        .param("standortnummer", requestDto.standortnummer())
                        .param("schulnummer", requestDto.schulnummer())
                        .param("userId", requestDto.userId())
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        List<Licence> actualLicences = new ObjectMapper().readValue(responseBody, new TypeReference<List<Licence>>() {});
        assertThat(actualLicences.stream().map(l -> l.licenceCode)).containsExactlyInAnyOrderElementsOf(expectedLicenceCodes);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidInput")
    void Authenticated_Request_WithInvalidBody_Returns_BadRequest(Bundesland bundesland, String standortnummer, String schulnummer, String userId) throws Exception {
        var requestDto = new LicencesRequestDto(bundesland, standortnummer, schulnummer, userId);

        mockMvc.perform(
                get("/v1/licences/request")
                        .header(API_KEY_HEADER, correctApiKey)
                        .param("clientName", GENERIC_LICENCES_TEST_CLIENT_NAME)
                        .param("bundesland", requestDto.bundesland() != null ? requestDto.bundesland().name() : null)
                        .param("standortnummer", requestDto.standortnummer())
                        .param("schulnummer", requestDto.schulnummer())
                        .param("userId", requestDto.userId())
        ).andExpect(status().isBadRequest());
    }

    @Test
    void Authenticated_Request_WithInvalidClientName_Returns_emptyResponse() throws Exception {
        var requestDto = new RelaxedLicencesRequestDto(Bundesland.BY.name(), null, null, null);
        String unregisteredClientName = "unregistered client name";

        var result = mockMvc.perform(
                get("/v1/licences/request")
                        .header(API_KEY_HEADER, correctApiKey)
                        .param("clientName", unregisteredClientName)
                        .param("bundesland", requestDto.bundesland())
                        .param("standortnummer", requestDto.standortnummer())
                        .param("schulnummer", requestDto.schulnummer())
                        .param("userId", requestDto.userId())
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertThat(result).isEqualTo("[]");
    }

    private static Stream<Arguments> provideValidInputAndOutput() {
        return Stream.of(
                Arguments.of(Bundesland.BB, null, null, null, List.of()),
                Arguments.of(Bundesland.BY, null, null, null, List.of("BY_1_23ui4g23c")),
                Arguments.of(Bundesland.BY, "ORT1", null, null, List.of("BY_1_23ui4g23c", "ORT1_LIZENZ_1")),
                Arguments.of(Bundesland.BY, "ORT1", "f3453b", null, List.of("BY_1_23ui4g23c", "ORT1_LIZENZ_1", "F3453_LIZENZ_1", "F3453_LIZENZ_2")),
                Arguments.of(Bundesland.BY, "ORT1", "f3453b", "student.2", List.of("BY_1_23ui4g23c", "ORT1_LIZENZ_1", "F3453_LIZENZ_1", "F3453_LIZENZ_2", "UIOC_QWUE_QASD_REIJ", "HPOA_SJKC_EJKA_WHOO"))
        );
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record RelaxedLicencesRequestDto(
            @JsonProperty() String bundesland,
            @JsonProperty() String standortnummer,
            @JsonProperty() String schulnummer,
            @JsonProperty() String userId) {
    }

    private static Stream<Arguments> provideInvalidInput() {
        return Stream.of(
                Arguments.of(null, null, null, null),
                Arguments.of(null, "ORT1", null, null),
                Arguments.of(null, null, "f3453b", null),
                Arguments.of(null, null, null, "student.2"),
                Arguments.of(Bundesland.BY, null, "f3453b", null),
                Arguments.of(Bundesland.BY, null, null, "student.2"),
                Arguments.of(Bundesland.BY, null, "f3453b", "student.2"),
                Arguments.of(Bundesland.BY, "ORT1", null, "student.2")
        );
    }
}
