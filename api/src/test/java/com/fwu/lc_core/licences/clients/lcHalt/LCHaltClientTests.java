package com.fwu.lc_core.licences.clients.lcHalt;

import com.fwu.lc_core.licences.models.ODRLAction;
import com.fwu.lc_core.shared.Bundesland;
import com.fwu.lc_core.shared.LicenceHolder;
import com.fwu.lc_core.licences.models.ODRLPolicy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static com.fwu.lc_core.shared.Constants.API_KEY_HEADER;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void RequestPermissions_GivenUserID_Yields_Result() throws ParserConfigurationException, IOException, SAXException {
        var expectedUserId = "test_user_id";
        var expectedMediaId = "test_media_id";
        var expectedLicencedMedia = List.of(Map.of("id", expectedMediaId));
        var expectedLicencedMediaIds = expectedLicencedMedia.stream().map(media -> media.get("id")).toList();
        var testLicences = Map.of(
                "user_id", expectedUserId,
                "licenced_media", expectedLicencedMedia
        );

        postTestLicences(testLicences);

        var permissions = lchaltClient.getPermissions(null, null, null, expectedUserId).block();

        assertThat(permissions).isNotNull();
        assertThat(permissions.size()).isEqualTo(expectedLicencedMedia.size());
        for (ODRLPolicy.Permission p : permissions) {
            assertThat(p.assigner).isEqualTo(LicenceHolder.LC_HALT);
            assertThat(p.action).isEqualTo(ODRLAction.Use);
            assertThat(p.target).isIn(expectedLicencedMediaIds);
        }
    }

    @Test
    public void RequestPermissions_GivenBundeslandAndSchulId_Yields_Result() throws ParserConfigurationException, IOException, SAXException {
        var expectedUserId = "test_user_id";
        var expectedBundesland = Bundesland.BY;
        var expectedSchulId = "test_schul_id";
        var expectedMediaId = "test_media_id";
        var expectedLicencedMedia = List.of(Map.of("id", expectedMediaId));
        var expectedLicencedMediaIds = expectedLicencedMedia.stream().map(media -> media.get("id")).toList();
        var testLicences = Map.of(
                "bundesland_id", expectedBundesland.toString(),
                "schul_id", expectedSchulId,
                "licenced_media", expectedLicencedMedia
        );

        postTestLicences(testLicences);

        var permissions = lchaltClient.getPermissions(expectedBundesland, null, expectedSchulId, expectedUserId).block();

        assertThat(permissions).isNotNull();
        assertThat(permissions.size()).isEqualTo(expectedLicencedMedia.size());
        for (ODRLPolicy.Permission p : permissions) {
            assertThat(p.assigner).isEqualTo(LicenceHolder.LC_HALT);
            assertThat(p.action).isEqualTo(ODRLAction.Use);
            assertThat(p.target).isIn(expectedLicencedMediaIds);
        }
    }

    @Test
    public void RequestPermissions_GivenUserIdAndBundesland_ThrowsException() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                lchaltClient.getPermissions(null, null, "test_schul_id", "test_user_id").block()
        );

        assertThat(exception.getMessage()).isEqualTo("If schulnummer is provided, bundesland must also be provided.");
    }

    @Test
    public void RequestPermissions_GivenNoIdentifiers_ThrowsException() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                lchaltClient.getPermissions(null, null, null, null).block()
        );

        assertThat(exception.getMessage()).isEqualTo("You must provide a userId parameter.");
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
