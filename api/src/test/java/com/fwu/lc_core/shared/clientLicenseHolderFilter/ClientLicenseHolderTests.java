package com.fwu.lc_core.shared.clientLicenseHolderFilter;

import com.fwu.lc_core.shared.LicenceHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;
import java.util.EnumSet;

import static com.fwu.lc_core.shared.Constants.API_KEY_HEADER;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
@AutoConfigureWebTestClient
public class ClientLicenseHolderTests {
    @Autowired
    private WebTestClient webTestClient;

    @Value("${vidis.api-key.unprivileged}")
    private String nonAdminApiKey;

    @Value("${admin.api-key}")
    private String adminApiKey;

    @Autowired
    private ClientLicenseHolderFilterService clientLicenseHolderFilterService;
    @Autowired
    private ClientLicenceHolderMappingRepository clientLicenceHolderMappingRepository;

    @BeforeEach
    void setUp() {
        clientLicenceHolderMappingRepository.deleteAll();
        webTestClient = webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Test
    void getRequestWithoutApiKeyForNonExistingClientNameReturnsUnauthorized() {
        var clientName = "nonExistingClientName";

        webTestClient.get()
                .uri("/admin/client-licence-holder-mapping/" + clientName)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void getRequestWithoutApiKeyForExistingClientNameReturnsUnauthorized() {
        var clientName = "clientName";

        clientLicenseHolderFilterService.setAllowedLicenceHolders(clientName, EnumSet.of(LicenceHolder.ARIX));

        webTestClient.get()
                .uri("/admin/client-licence-holder-mapping/" + clientName)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void getRequestWithNonPrivilegedApiKeyForNonExistingClientNameReturnsUnauthorized() {
        var clientName = "nonExistingClientName";

        webTestClient.get()
                .uri("/admin/client-licence-holder-mapping/" + clientName)
                .header(API_KEY_HEADER, nonAdminApiKey)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void getRequestWithNonPrivilegedApiKeyForExistingClientNameReturnsUnauthorized() {
        var clientName = "clientName";

        clientLicenseHolderFilterService.setAllowedLicenceHolders(clientName, EnumSet.of(LicenceHolder.ARIX));

        webTestClient.get()
                .uri("/admin/client-licence-holder-mapping/" + clientName)
                .header(API_KEY_HEADER, nonAdminApiKey)
                .exchange()
                .expectStatus().isForbidden();
    }
}
