package com.fwu.lc_core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fwu.lc_core.shared.LicenceHolder;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.ClientLicenceHolderMappingRepository;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.ClientLicenseHolderFilterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;
import java.util.EnumSet;

import static com.fwu.lc_core.shared.Constants.API_KEY_HEADER;
import static com.fwu.lc_core.shared.clientLicenseHolderFilter.loggingAssertions.assertThatBothLogsHaveTheSameTraceId;
import static com.fwu.lc_core.shared.clientLicenseHolderFilter.loggingAssertions.assertThatFirstLogComesBeforeSecondLog;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
@AutoConfigureWebTestClient
@EnabledIfSystemProperty(named="spring.profiles.active", matches = ".*local.*")
class BiloV2Tests {

    public static final String BILO_TEST_CLIENT_NAME = "bilo v2 test client id";
    @Autowired
    private WebTestClient webTestClient;

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
        clientLicenseHolderFilterService.setAllowedLicenceHolders(BILO_TEST_CLIENT_NAME, EnumSet.of(LicenceHolder.BILO_V2));
        webTestClient = webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Test
    void requestWithoutApiKey() {
        webTestClient
                .get()
                .uri("/v1/bilo/request/" + dummyUserId)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void requestWithWrongApiKey() {
        webTestClient
                .get()
                .uri("/v1/bilo/request/" + dummyUserId)
                .header(API_KEY_HEADER, "wrong-api-key")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void requestWithLowercaseApiKeyHeaderName() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/bilo/request/" + dummyUserId)
                        .queryParam("clientName", BILO_TEST_CLIENT_NAME).build())
                .header("x-api-key", correctApiKey)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void requestWithUppercaseApiKeyHeaderName() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/bilo/request/" + dummyUserId)
                        .queryParam("clientName", BILO_TEST_CLIENT_NAME).build())
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void requestWithIncorrectInfo() {
        webTestClient
                .get()
                .uri("/v1/bilo/request")
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void requestWithCorrectInfoButWrongVerb() {
        webTestClient
                .post()
                .uri("/v1/bilo/request/" + dummyUserId)
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isEqualTo(405);
    }

    @Test
    void requestWithCorrectInfoButUnconfiguredClient() {
        String clientId = "unconfigured-client";

        var responseBody = webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/bilo/request/" + dummyUserId)
                        .queryParam("clientName", clientId).build())
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isEqualTo("[]");
    }


    @Test
    void requestWithCorrectInfo() throws Exception {
        var responseBody = webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/bilo/request/" + dummyUserId)
                        .queryParam("clientName", BILO_TEST_CLIENT_NAME).build())
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult().getResponseBody();

        String cannedResponse = "{\"user\":{\"id\":\"" + dummyUserId + "\",\"first_name\":\"student\",\"last_name\":\"2\",\"user_alias\":null,\"roles\":[\"student\"],\"media\":[]},\"organizations\":[{\"id\":\"testfwu\",\"org_type\":\"school\",\"identifier\":null,\"authority\":null,\"name\":\"testfwu\",\"roles\":[\"student\"],\"media\":[],\"groups\":[{\"id\":\"1\",\"name\":\"1\",\"group_type\":\"class\",\"media\":[]}]}]}";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode expected = objectMapper.readTree(cannedResponse);
        JsonNode actual = objectMapper.readTree(responseBody);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void requestWithNonExistingUser() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/bilo/request/" + dummyUserId + "123")
                        .queryParam("clientName", BILO_TEST_CLIENT_NAME).build())
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void licenceRequest_Logs_Missing_Permissions(CapturedOutput output) {
        String clientName = "unprivileged-client";
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/bilo/request/" + dummyUserId)
                        .queryParam("clientName", clientName).build())
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isOk();

        assertThat(output.getOut()).contains("Client " + clientName + " is not allowed to access BILO_V2");
    }

    @Test
    void licenceRequest_Logs_Result_Count(CapturedOutput output) {
        String expectedFirstLog = "Received licence request for client: " + BILO_TEST_CLIENT_NAME;
        String expectedSecondLog = "Found licences for client: " + BILO_TEST_CLIENT_NAME;

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/bilo/request/" + dummyUserId)
                        .queryParam("clientName", BILO_TEST_CLIENT_NAME).build())
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isOk();

        String logs = output.getOut();
        assertThat(logs).contains(expectedFirstLog);
        assertThat(logs).contains(expectedSecondLog);
        assertThat(logs).contains("userId: " + dummyUserId);
        assertThatFirstLogComesBeforeSecondLog(logs, expectedFirstLog, expectedSecondLog);
        assertThatBothLogsHaveTheSameTraceId(logs, expectedFirstLog, expectedSecondLog);
    }

    @Test
    void licenceRequest_Logs_Empty(CapturedOutput output) {
        String expectedClientLog = "Received licence request for client: " + BILO_TEST_CLIENT_NAME;
        String notExpectedResultsLog = "Found licences for client: " + BILO_TEST_CLIENT_NAME;

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/bilo/request/non-existing-user-id")
                        .queryParam("clientName", BILO_TEST_CLIENT_NAME).build())
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isNotFound();

        String logs = output.getOut();
        assertThat(logs).contains(expectedClientLog);
        assertThat(logs).doesNotContain(notExpectedResultsLog);
    }
}
