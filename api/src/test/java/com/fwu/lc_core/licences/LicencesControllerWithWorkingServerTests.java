package com.fwu.lc_core.licences;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fwu.lc_core.config.ClassNameRetriever;
import com.fwu.lc_core.licences.models.ODRLPolicy;
import com.fwu.lc_core.shared.LicenceHolder;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.ClientLicenceHolderMappingRepository;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.ClientLicenseHolderFilterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
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
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

import static com.fwu.lc_core.licences.TestHelper.extractLicenceCodesFrom;
import static com.fwu.lc_core.shared.Constants.API_KEY_HEADER;
import static com.fwu.lc_core.shared.clientLicenseHolderFilter.loggingAssertions.assertThatBothLogsHaveTheSameTraceId;
import static com.fwu.lc_core.shared.clientLicenseHolderFilter.loggingAssertions.assertThatFirstLogComesBeforeSecondLog;
import static org.assertj.core.api.AssertionsForClassTypes.fail;
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
    @Autowired
    ClassNameRetriever classNameRetriever;
    @Autowired
    ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        clientLicenceHolderMappingRepository.deleteAll();
        clientLicenseHolderFilterService.setAllowedLicenceHolders(GENERIC_LICENCES_TEST_CLIENT_NAME, EnumSet.of(LicenceHolder.ARIX));
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
        var requestDto = new RelaxedLicencesRequestDto("BY", null, null, null);

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
    void RequestWhichReturnsErrorDoesNotReturnTrace() {
        var requestDto = new RelaxedLicencesRequestDto("Non-existent Bundesland", "STR", null, "qwr");
        var test = classNameRetriever.getAllClassNames(applicationContext);

        var result = webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/licences/request")
                        .queryParam("clientName", "cat")
                        .queryParam("bundesland", requestDto.bundesland())
                        .queryParam("standortnummer", requestDto.standortnummer())
                        .queryParam("userId", requestDto.userId()).build())
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isBadRequest()
                .returnResult(String.class)
                .getResponseBody().blockFirst();

        assertThat(result).doesNotContain(test);
    }


    @ParameterizedTest
    @MethodSource("provideInvalidInput")
    void Authenticated_Request_WithInvalidBundesland_Returns_BadRequest(String bundesland, String standortnummer, String schulnummer, String userId) {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/licences/request")
                        .queryParam("clientName", GENERIC_LICENCES_TEST_CLIENT_NAME)
                        .queryParam("bundesland", bundesland)
                        .queryParam("standortnummer", standortnummer)
                        .queryParam("schulnummer", schulnummer)
                        .queryParam("userId", userId).build())
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @ParameterizedTest
    @MethodSource("provideValidInputAndOutput")
    @EnabledIfSystemProperty(named = "spring.profiles.active", matches = ".*local.*")
    void Authenticated_Request_WithValidBody_Returns_CorrectLicences(String bundesland, String standortnummer, String schulnummer, String userId, List<String> expectedLicenceCodes) {
        var response = webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/licences/request")
                        .queryParam("clientName", GENERIC_LICENCES_TEST_CLIENT_NAME)
                        .queryParam("bundesland", bundesland)
                        .queryParam("standortnummer", standortnummer)
                        .queryParam("schulnummer", schulnummer)
                        .queryParam("userId", userId).build())
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ODRLPolicy>() {
                })
                .returnResult()
                .getResponseBody();

        if (response == null) {
            fail();
        } else {
            assertThat(extractLicenceCodesFrom(response)).containsExactlyInAnyOrderElementsOf(expectedLicenceCodes);
        }
    }

    @Test
    void Authenticated_Request_WithInvalidClientName_Returns_emptyResponse() {
        var requestDto = new RelaxedLicencesRequestDto("BY", null, null, null);
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
                .expectBody(new ParameterizedTypeReference<ODRLPolicy>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(extractLicenceCodesFrom(result).size()).isEqualTo(0);
    }

    @Test
    void licenceRequest_Logs_Request(CapturedOutput output) {

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/licences/request")
                        .queryParam("clientName", GENERIC_LICENCES_TEST_CLIENT_NAME)
                        .queryParam("bundesland", "STK")
                        .build())
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isOk();

        assertThat(output.getOut()).contains("GET /v1/licences/request: clientName: " + GENERIC_LICENCES_TEST_CLIENT_NAME);
        assertThat(output.getOut()).contains("Found ");
        assertThat(output.getOut()).contains(" licences in total for client: " + GENERIC_LICENCES_TEST_CLIENT_NAME);
    }

    @Test
    @EnabledIfSystemProperty(named = "spring.profiles.active", matches = ".*local.*")
    void licenceRequest_Logs_Result_Count(CapturedOutput output) {
        String expectedFirstLog = "GET /v1/licences/request: clientName: " + GENERIC_LICENCES_TEST_CLIENT_NAME;
        String expectedSecondLog = "Found 6 licences in total for client: " + GENERIC_LICENCES_TEST_CLIENT_NAME;

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/licences/request")
                        .queryParam("clientName", GENERIC_LICENCES_TEST_CLIENT_NAME)
                        .queryParam("bundesland", "BY")
                        .queryParam("standortnummer", "ORT1")
                        .queryParam("schulnummer", "f3453b")
                        .queryParam("userId", "student.2").build())
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
                Arguments.of("BB", null, null, null, List.of()),
                Arguments.of("DE-BB", null, null, null, List.of()),
                Arguments.of("BY", null, null, null, List.of("BY_1_23ui4g23c")),
                Arguments.of("BY", "ORT1", null, null, List.of("BY_1_23ui4g23c", "ORT1_LIZENZ_1")),
                Arguments.of("BY", "ORT1", "f3453b", null, List.of("BY_1_23ui4g23c", "ORT1_LIZENZ_1", "F3453_LIZENZ_1", "F3453_LIZENZ_2")),
                Arguments.of("BY", "ORT1", "f3453b", "student.2", List.of("BY_1_23ui4g23c", "ORT1_LIZENZ_1", "F3453_LIZENZ_1", "F3453_LIZENZ_2", "UIOC_QWUE_QASD_REIJ", "HPOA_SJKC_EJKA_WHOO"))
        );
    }

    private static Stream<Arguments> provideInvalidInput() {
        return Stream.of(
                Arguments.of("ABC", null, null, null),
                Arguments.of("DE_BB", null, null, null),
                Arguments.of("BBB", null, null, null)
        );
    }
}


@SpringBootTest
@AutoConfigureWebTestClient
@TestPropertySource(properties = {
        "server.error.include-message=always",
        "server.error.include-binding-errors=always",
        "server.error.include-stacktrace=always",
        "server.error.include-exception=true"
})
@ExtendWith(OutputCaptureExtension.class)
class LicencesControllerWithWorkingServerAndFullyVerboseSettingsTests {
    private static final String GENERIC_LICENCES_TEST_CLIENT_NAME = "generic licences test client name";
    @Autowired
    private WebTestClient webTestClient;

    @Value("${vidis.api-key.unprivileged}")
    private String correctApiKey;
    @Autowired
    private ClientLicenseHolderFilterService clientLicenseHolderFilterService;
    @Autowired
    private ClientLicenceHolderMappingRepository clientLicenceHolderMappingRepository;
    @Autowired
    ClassNameRetriever classNameRetriever;
    @Autowired
    ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        clientLicenceHolderMappingRepository.deleteAll();
        clientLicenseHolderFilterService.setAllowedLicenceHolders(GENERIC_LICENCES_TEST_CLIENT_NAME, EnumSet.of(LicenceHolder.ARIX));
    }

    @Test
    void RequestWhichReturnsTrace() {
        var requestDto = new RelaxedLicencesRequestDto("Non-existent Bundesland", "STR", null, "qwr");
        var allExistingBeanNames = classNameRetriever.getAllClassNames(applicationContext);

        var result = webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/licences/request")
                        .queryParam("clientName", "cat")
                        .queryParam("bundesland", requestDto.bundesland())
                        .queryParam("standortnummer", requestDto.standortnummer())
                        .queryParam("userId", requestDto.userId()).build())
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isBadRequest()
                .returnResult(String.class)
                .getResponseBody().blockFirst();

        assertThat(result).containsAnyOf(allExistingBeanNames.toArray(new String[0]));
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
record RelaxedLicencesRequestDto(
        @JsonProperty String bundesland,
        @JsonProperty String standortnummer,
        @JsonProperty String schulnummer,
        @JsonProperty String userId) {
}
