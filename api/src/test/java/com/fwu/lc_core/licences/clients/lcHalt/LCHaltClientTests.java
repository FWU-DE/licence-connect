package com.fwu.lc_core.licences.clients.lcHalt;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import static com.fwu.lc_core.shared.Constants.API_KEY_HEADER;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class LCHaltClientTests {

    @Value("${lc-halt.domain}")
    private String lcHaltDomain;

    @Value("${lc-halt.admin-api-key}")
    private String lcHaltAdminApiKey;

    @Autowired
    LCHaltClient lchaltClient;

    @BeforeEach
    public void setup() {
        // Clean up licences from lc halt before each test
        removeAllTestLicences();
    }

    // @Test
    // public void RequestPermissions_GivenBundeslandAndStandortnummer_Yields_Result() throws ParserConfigurationException, IOException, SAXException {
    //     var expectedBundesland = Bundesland.BY;
    //     var expectedStandortnummer = "test_standortnummer";
    //     var expectedMediaId = "test_media_id";
    //     var expectedLicencedMedia = List.of(Map.of("id", expectedMediaId));
    //     var expectedLicencedMediaIds = expectedLicencedMedia.stream().map(media -> media.get("id")).toList();
    //     var testLicences = Map.of(
    //             "bundesland_id", expectedBundesland.toISOIdentifier(),
    //             "standortnummer", expectedStandortnummer,
    //             "licenced_media", expectedLicencedMedia
    //     );

    //     postTestLicences(testLicences);

    //     var permissions = lchaltClient.getPermissions(expectedBundesland, expectedStandortnummer, null).block();

    //     assertThat(permissions).isNotNull();
    //     assertThat(permissions.size()).isEqualTo(expectedLicencedMedia.size());
    //     for (ODRLPolicy.Permission p : permissions) {
    //         assertThat(p.assigner).isEqualTo(LicenceHolder.LC_HALT);
    //         assertThat(p.action).isEqualTo(ODRLAction.Use);
    //         assertThat(p.target).isIn(expectedLicencedMediaIds);
    //     }
    // }

    @Test
    public void RequestPermissions_GivenNoBundesland_ThrowsException() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                lchaltClient.getPermissions(null, null, null).block()
        );

        assertThat(exception.getMessage()).isEqualTo("Bundesland is required.");
    }

    // Helper methods for setup and teardown
    private void postTestLicences(Map<String, Object> licences) {
        var webClient = getLCHaltMediaAssignemtWebClient();

        webClient
                .post()
                .bodyValue(licences)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private void removeAllTestLicences() {
        var webClient = getLCHaltMediaAssignemtWebClient();

        var licences = webClient.get()
                .retrieve()
                .bodyToFlux(Map.class)
                .collectList()
                .block();

        assert(licences != null);

        licences.stream()
                .map(media -> media.get("id").toString())
                .forEach(id ->
                    webClient.delete()
                            .uri(id)
                            .retrieve()
                            .bodyToMono(String.class)
                            .block()
                );
    }

    private WebClient getLCHaltMediaAssignemtWebClient() {
        return WebClient.builder()
                .baseUrl(lcHaltDomain + "/admin/media-licence-assignment/")
                .defaultHeader(API_KEY_HEADER, lcHaltAdminApiKey)
                .build();
    }
}
