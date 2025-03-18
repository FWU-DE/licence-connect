package com.fwu.lc_core.licences;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fwu.lc_core.licences.models.Licence;
import com.fwu.lc_core.licences.models.LicencesRequestDto;
import com.fwu.lc_core.shared.Bundesland;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.AvailableLicenceHolders;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.ClientLicenceHolderMappingRepository;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.ClientLicenseHolderFilterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

import static com.fwu.lc_core.shared.Constants.API_KEY_HEADER;
import static com.fwu.lc_core.shared.clientLicenseHolderFilter.loggingAssertions.assertThatBothLogsHaveTheSameTraceId;
import static com.fwu.lc_core.shared.clientLicenseHolderFilter.loggingAssertions.assertThatFirstLogComesBeforeSecondLog;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
@ExtendWith(OutputCaptureExtension.class)
class LicencesControllerWithWorkingServerTests {
    private static final String GENERIC_LICENCES_TEST_CLIENT_NAME = "generic licences test client name";
    @Autowired
    private WebTestClient webTestClient;

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
    void Unauthenticated_Request_Returns_Forbidden() {
        webTestClient
                .get()
                .uri("/v1/licences/request")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void Authenticated_Request_Without_Params_Returns_BadRequest() {
        webTestClient
                .get()
                .uri("/v1/licences/request")
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void Authenticated_Request_Without_ClientName_Parameter_Returns_BadRequest() {
        var requestDto = new RelaxedLicencesRequestDto(Bundesland.BY.value, null, null, null);

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/licences/request")
                        .queryParam("bundesland", requestDto.bundesland())
                        .queryParam("standortnummer", requestDto.standortnummer())
                        .queryParam("schulnummer", requestDto.schulnummer())
                        .queryParam("userId", requestDto.userId()).build())
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void Authenticated_Request_WithInvalidBundesland_Returns_BadRequest() {
        var requestDto = new RelaxedLicencesRequestDto("ABC", null, null, null);

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/licences/request")
                        .queryParam("clientName", GENERIC_LICENCES_TEST_CLIENT_NAME)
                        .queryParam("bundesland", requestDto.bundesland())
                        .queryParam("standortnummer", requestDto.standortnummer())
                        .queryParam("schulnummer", requestDto.schulnummer())
                        .queryParam("userId", requestDto.userId()).build())
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @IfProfileValue(name = "spring.profiles.active", value = "local")
    @ParameterizedTest
    @MethodSource("provideValidInputAndOutput")
    void Authenticated_Request_WithValidBody_Returns_CorrectLicences(Bundesland bundesland, String standortnummer, String schulnummer, String userId, List<String> expectedLicenceCodes) {
        var requestDto = new LicencesRequestDto(bundesland, standortnummer, schulnummer, userId);
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/licences/request")
                        .queryParam("clientName", GENERIC_LICENCES_TEST_CLIENT_NAME)
                        .queryParam("bundesland", requestDto.bundesland().name())
                        .queryParam("standortnummer", requestDto.standortnummer())
                        .queryParam("schulnummer", requestDto.schulnummer())
                        .queryParam("userId", requestDto.userId()).build())
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Licence>>() {
                })
                .value(licences -> {
                    assertThat(licences.stream().map(l -> l.licenceCode))
                            .containsExactlyInAnyOrderElementsOf(expectedLicenceCodes);
                });
    }

    @Test
    void Authenticated_Request_WithValidBody_STK_Returns_CorrectLicences()  {
        var requestDto = new RelaxedLicencesRequestDto("STK", "STR", null, null);
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/licences/request")
                        .queryParam("clientName", GENERIC_LICENCES_TEST_CLIENT_NAME)
                        .queryParam("bundesland", requestDto.bundesland())
                        .queryParam("standortnummer", requestDto.standortnummer())
                        .queryParam("schulnummer", requestDto.schulnummer())
                        .queryParam("userId", requestDto.userId())
                        .build())
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Licence>>() {})
                .value(licences -> assertThat(licences.stream().map(l -> l.licenceCode)).isNotEmpty());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidInput")
    void Authenticated_Request_WithInvalidBody_Returns_BadRequest(Bundesland bundesland, String standortnummer, String schulnummer, String userId) {
        var requestDto = new LicencesRequestDto(bundesland, standortnummer, schulnummer, userId);

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/licences/request")
                        .queryParam("clientName", GENERIC_LICENCES_TEST_CLIENT_NAME)
                        .queryParam("bundesland", requestDto.bundesland() != null ? requestDto.bundesland().name() : null)
                        .queryParam("standortnummer", requestDto.standortnummer())
                        .queryParam("schulnummer", requestDto.schulnummer())
                        .queryParam("userId", requestDto.userId()).build())
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void Authenticated_Request_WithInvalidClientName_Returns_emptyResponse() {
        var requestDto = new RelaxedLicencesRequestDto(Bundesland.BY.name(), null, null, null);
        String unregisteredClientName = "unregistered client name";

        var result = webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/licences/request")
                        .queryParam("clientName", unregisteredClientName)
                        .queryParam("bundesland", requestDto.bundesland())
                        .queryParam("standortnummer", requestDto.standortnummer())
                        .queryParam("schulnummer", requestDto.schulnummer())
                        .queryParam("userId", requestDto.userId()).build())
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertThat(result).isEqualTo("[]");
    }

    @Test
    void licenceRequest_Logs_Request(CapturedOutput output) {
        var requestDto = new LicencesRequestDto(Bundesland.STK, "STR", null, null);

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/licences/request")
                        .queryParam("clientName", GENERIC_LICENCES_TEST_CLIENT_NAME)
                        .queryParam("bundesland", requestDto.bundesland().name())
                        .queryParam("standortnummer", requestDto.standortnummer())
                        .queryParam("schulnummer", requestDto.schulnummer())
                        .queryParam("userId", requestDto.userId()).build())
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isOk();

        assertThat(output.getOut()).contains("Received licence request for client: " + GENERIC_LICENCES_TEST_CLIENT_NAME);
        assertThat(output.getOut()).contains("Found ");
        assertThat(output.getOut()).contains(" licences for client: " + GENERIC_LICENCES_TEST_CLIENT_NAME);
    }

    @IfProfileValue(name = "spring.profiles.active", value = "local")
    @Test
    void licenceRequest_Logs_Result_Count(CapturedOutput output) {
        var requestDto = new LicencesRequestDto(Bundesland.BY, "ORT1", "f3453b", "student.2");
        String expectedFirstLog = "Received licence request for client: " + GENERIC_LICENCES_TEST_CLIENT_NAME;
        String expectedSecondLog = "Found 6 licences for client: " + GENERIC_LICENCES_TEST_CLIENT_NAME;

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/licences/request")
                        .queryParam("clientName", GENERIC_LICENCES_TEST_CLIENT_NAME)
                        .queryParam("bundesland", requestDto.bundesland().name())
                        .queryParam("standortnummer", requestDto.standortnummer())
                        .queryParam("schulnummer", requestDto.schulnummer())
                        .queryParam("userId", requestDto.userId()).build())
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isOk();

        String logs = output.getOut();
        assertThat(logs).contains(expectedFirstLog);
        assertThat(logs).contains(expectedSecondLog);
        assertThatFirstLogComesBeforeSecondLog(logs, expectedFirstLog, expectedSecondLog);
        assertThatBothLogsHaveTheSameTraceId(logs, expectedFirstLog, expectedSecondLog);
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
