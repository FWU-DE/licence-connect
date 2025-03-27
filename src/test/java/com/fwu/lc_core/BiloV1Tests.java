package com.fwu.lc_core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;
import java.util.EnumSet;
import java.util.stream.Stream;

import static com.fwu.lc_core.shared.Constants.API_KEY_HEADER;
import static com.fwu.lc_core.shared.clientLicenseHolderFilter.loggingAssertions.assertThatBothLogsHaveTheSameTraceId;
import static com.fwu.lc_core.shared.clientLicenseHolderFilter.loggingAssertions.assertThatFirstLogComesBeforeSecondLog;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
@AutoConfigureWebTestClient
class BiloV1Tests {

    public static final String BILO_TEST_CLIENT_NAME = "bilo v1 test client id";
    public static final String CANNED_RESPONSE_FROM_OLD_API = "{\"id\":\"student.2\",\"first_name\":\"student\",\"last_name\":\"2\",\"licenses\":[\"WES-VIDT-2369-P85R-KOUD\"],\"context\":{\"92490b9dc18341906b557bbd4071e1c97db9f9b65d348fafd30988b85a2f6924\":{\"school_name\":\"testfwu\",\"classes\":[{\"name\":\"1\",\"id\":\"cda6b1ddfa321de5a456c69fd5cee2cde7eeeae9b9d9ed24eb84fd88a35cfecb\",\"licenses\":[\"WES-VIDT-0346-P85R-KOUD\",\"WES-VIDT-9775-P85R-VWYX\"]}],\"roles\":[\"student\"],\"licenses\":[\"WES-VIDT-7368-P85R-KOUD\"]}}}";

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
        clientLicenseHolderFilterService.setAllowedLicenceHolders(BILO_TEST_CLIENT_NAME, EnumSet.of(AvailableLicenceHolders.BILO_V1));
        webTestClient = webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Test
    void requestWithoutApiKey() {
        webTestClient.get().uri("/v1/ucs/request").exchange().expectStatus().isUnauthorized();
    }

    @Test
    void requestWithWrongApiKey() {
        webTestClient
                .get()
                .uri("/v1/ucs/request")
                .header(API_KEY_HEADER, "wrong-api-key")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void requestWithLowercaseApiKeyHeaderName() {
        var requestDto = createValidUcsRequestDto();
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/ucs/request")
                        .queryParam("userId", requestDto.userId)
                        .queryParam("clientId", requestDto.clientId)
                        .queryParam("schulkennung", requestDto.schulkennung)
                        .queryParam("bundesland", requestDto.bundesland.toString()).build())
                .header("x-api-key", correctApiKey)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void requestWithUppercaseApiKeyHeaderName() {
        var requestDto = createValidUcsRequestDto();
        createRequest(requestDto)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void requestWithCorrectInfo() throws Exception {
        var requestDto = createValidUcsRequestDto();
        var responseBody = createRequest(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult().getResponseBody();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode expected = objectMapper.readTree(CANNED_RESPONSE_FROM_OLD_API);
        JsonNode actual = objectMapper.readTree(responseBody);
        assert actual.equals(expected) : "JSON objects are not the same";
    }

    @Test
    void requestWithCorrectInfoButUnconfiguredClient() {
        var requestDto = createValidUcsRequestDto("unconfigured-client");
        var responseBody = createRequest(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isEqualTo(null);
    }

    @ParameterizedTest
    @MethodSource("provideIncorrectInfo")
    void requestWithIncorrectInfo(String userId, String clientId, String schulkennung, String bundesland) {
        createRequestFromStringInfo(userId, clientId, schulkennung, bundesland)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void requestWithIncorrectBundesland() {
        var requestDto = createValidUcsRequestDto();
        String invalidBundesland = "XX";
        createRequestFromStringInfo(requestDto.userId, requestDto.clientId, requestDto.schulkennung, invalidBundesland)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void licenceRequest_Logs_Missing_Permissions(CapturedOutput output) {
        var requestDto = createValidUcsRequestDto();
        clientLicenseHolderFilterService.setAllowedLicenceHolders(BILO_TEST_CLIENT_NAME, EnumSet.noneOf(AvailableLicenceHolders.class));

        createRequest(requestDto)
                .exchange()
                .expectStatus().is2xxSuccessful();

        assertThat(output.getOut()).contains("Client " + BILO_TEST_CLIENT_NAME + " is not allowed to access BILO_V1");
    }

    @Test
    void licenceRequest_Logs_Result_Count(CapturedOutput output) {
        var requestDto = createValidUcsRequestDto();
        String expectedFirstLog = "Received licence request for client: " + BILO_TEST_CLIENT_NAME;
        String expectedSecondLog = "Found 1 licences for client: " + BILO_TEST_CLIENT_NAME;

        createRequest(requestDto)
                .exchange()
                .expectStatus().is2xxSuccessful();

        String logs = output.getOut();
        assertThat(logs).contains(expectedFirstLog);
        assertThat(logs).contains(expectedSecondLog);
        assertThatFirstLogComesBeforeSecondLog(logs, expectedFirstLog, expectedSecondLog);
        assertThat(output.getOut()).contains("Bundesland: " + requestDto.bundesland);
        assertThat(output.getOut()).contains("Schulkennung: " + requestDto.schulkennung);
        assertThat(output.getOut()).contains("UserId: " + requestDto.userId);
        assertThatBothLogsHaveTheSameTraceId(logs, expectedFirstLog, expectedSecondLog);
    }

    private WebTestClient.RequestHeadersSpec<?> createRequest(UcsRequestDto requestDto) {
        return webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/ucs/request")
                        .queryParam("userId", requestDto.userId)
                        .queryParam("clientId", requestDto.clientId)
                        .queryParam("schulkennung", requestDto.schulkennung)
                        .queryParam("bundesland", requestDto.bundesland.toString()).build())
                .header(API_KEY_HEADER, correctApiKey);
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

    private WebTestClient.RequestHeadersSpec<?> createRequestFromStringInfo(String userId, String clientId, String schulkennung, String bundesland) {
        return webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/ucs/request")
                        .queryParam("clientName", BILO_TEST_CLIENT_NAME)
                        .queryParam("userId", userId)
                        .queryParam("clientId", clientId)
                        .queryParam("schulkennung", schulkennung)
                        .queryParam("bundesland", bundesland).build())
                .header(API_KEY_HEADER, correctApiKey);
    }

    private static UcsRequestDto createValidUcsRequestDto() {
        return createValidUcsRequestDto(null);
    }

    private static UcsRequestDto createValidUcsRequestDto(String clientId) {
        return new UcsRequestDto("student.2", clientId == null ? BILO_TEST_CLIENT_NAME : clientId, null, Bundesland.MV);
    }

    private static class UcsRequestDto {
        public final String userId;
        public final String clientId;
        public final String schulkennung;
        public final Bundesland bundesland;

        public UcsRequestDto(String userId, String clientId, String schulkennung, Bundesland bundesland) {
            this.userId = userId;
            this.clientId = clientId;
            this.schulkennung = schulkennung;
            this.bundesland = bundesland;
        }
    }
}
