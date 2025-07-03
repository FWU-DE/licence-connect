package com.fwu.lc_core.licences.clients.lcHalt;

import com.fwu.lc_core.licences.models.ODRLAction;
import com.fwu.lc_core.shared.LicenceHolder;
import com.fwu.lc_core.licences.models.ODRLPolicy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
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

@Slf4j
@SpringBootTest
public class LCHaltClientTests {

    @Value("${lc-halt.domain}")
    private String lcHaltDomain;

    @Value("${lc-halt.admin-api-key}")
    private String lcHaltAdminApiKey;

    @Autowired
    LCHaltClient lchaltClient;

    // FIXME: For this whole test suite to work, the LC-Halt instance must be running.
    // Ideally, we'd start and stop it in a @BeforeAll and @AfterAll method, by running `bun run:lc-halt:container`
    // and docker compose down, respectively. I have not yet figured out how to properly do this in a Spring Boot test
    // context, as we do already have a docker compose for the tests, which is starting the other mock servers.
    @Test
    public void RequestPermissions_GivenUserID_Yields_Result() throws ParserConfigurationException, IOException, SAXException {

        var expectedUserId = "test_user_id";
        var expectedMediaId = "test_media_id";
        var expectedLicencedMedia = List.of(Map.of("id", expectedMediaId));
        var expectedLicencedMediaIds = expectedLicencedMedia.stream().map(media -> media.get("id")).toList();
        var testLicences = Map.of(
                "user_id", expectedUserId,
//                "bundesland_id", "string",
//                "schul_id", "string",
                "licenced_media", expectedLicencedMedia
        );

        var webClient = WebClient.builder().baseUrl(lcHaltDomain).build();

        // CAUTION: FIXME: This will add new licences without cleaning up afterwards.
        //  Thus, the `size()` and the `isIn` checks below will fail if run multiple times.
        // TODO: Add cleanup to remove test licences from LC-Halt after the test runs.
        webClient.post()
                .uri("/admin/media-licence-assignment/")
                .header(API_KEY_HEADER, lcHaltAdminApiKey)
                .bodyValue(testLicences)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        var permissions = lchaltClient.getPermissions(null, null, null, expectedUserId).block();

        assertThat(permissions).isNotNull();
//        assertThat(permissions.size()).isEqualTo(expectedLicencedMedia.size());
        for (ODRLPolicy.Permission p : permissions) {
            assertThat(p.assigner).isEqualTo(LicenceHolder.LC_HALT);
            assertThat(p.action).isEqualTo(ODRLAction.Use);
//            assertThat(p.target).isIn(expectedLicencedMediaIds);
        }
    }
}
