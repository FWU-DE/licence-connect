package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.clients.lcHalt.LCHaltClient;
import com.fwu.lc_core.shared.LicenceHolder;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.ClientLicenceHolderMappingRepository;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.ClientLicenseHolderFilterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.EnumSet;

import static com.fwu.lc_core.shared.Constants.API_KEY_HEADER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(properties = {"arix.url=${arix.rejecting.url}"})
@AutoConfigureWebTestClient
@ExtendWith(OutputCaptureExtension.class)
@Import(LicencesControllerWithFailingArixServerTests.CustomTestConfig.class)
class LicencesControllerWithFailingArixServerTests {
    private static final String GENERIC_LICENCES_TEST_CLIENT_NAME = "generic licences test client name";
    @Autowired
    private WebTestClient webTestClient;

    @Value("${vidis.api-key.unprivileged}")
    private String correctApiKey;
    @Autowired
    private ClientLicenceHolderMappingRepository clientLicenceHolderMappingRepository;
    @Autowired
    private ClientLicenseHolderFilterService clientLicenseHolderFilterService;

    @TestConfiguration
    static class CustomTestConfig {
        @Bean
        @Primary // Ensures this bean overrides the default one in the context
        public LicencesCollector licencesCollector() {
            return new LicencesCollector();
        }
    }

    @BeforeEach
    void setUp() {
        clientLicenceHolderMappingRepository.deleteAll();
        clientLicenseHolderFilterService.setAllowedLicenceHolders(GENERIC_LICENCES_TEST_CLIENT_NAME, EnumSet.of(LicenceHolder.ARIX));
    }

    @Test
    void licenceRequest_With_Error_Logs_Error(CapturedOutput output) {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/licences/request")
                        .queryParam("clientName", GENERIC_LICENCES_TEST_CLIENT_NAME)
                        .queryParam("bundesland", "BY")
                        .queryParam("standortnummer", "INVALID").build())
                .header(API_KEY_HEADER, correctApiKey)
                .exchange()
                .expectStatus().isOk();

        assertThat(output.getOut()).contains("Error fetching licences from ARIX: <error>Sorry, interface search not allowed for your IP</error>");
    }
}
