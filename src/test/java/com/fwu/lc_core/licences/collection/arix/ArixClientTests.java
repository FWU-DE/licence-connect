package com.fwu.lc_core.licences.collection.arix;

import com.fwu.lc_core.DockerComposeManager;
import com.fwu.lc_core.shared.Bundesland;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
//import reactor.test.StepVerifier;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(DockerComposeManager.class)
public class ArixClientTests {
    @Value("${important.url}")
    private String baseUrl;
    @Value("${important.url.rejecting}")
    private String baseUrlRejecting;

    @Test
    public void RequestLicences_BundeslandOnly_ReturnsLicencesWhichAreIssuedToEverybodyOfTheBundesland() {
        var arixClient = new ArixClient(baseUrl);
        var licences = arixClient.GetLicences(Bundesland.valueOf("BY"), null, null, null).block();

        assertThat(licences.source).isEqualTo("ARIX");
        assertThat(licences.rawResult).isEqualTo("<result><r identifier=\"BY_1_23ui4g23c\"><f n=\"nr\">BY_1_23ui4g23c</f></r></result>");
    }

    @Test
    public void RequestLicences_Bundesland_and_Standort_ReturnsBundesland_u_Standort() {
        var arixClient = new ArixClient(baseUrl);
        var licences = arixClient.GetLicences(Bundesland.valueOf("BY"), "ORT1", null, null).block();

        assertThat(licences.source).isEqualTo("ARIX");
        assertThat(licences.rawResult).isEqualTo("<result><r identifier=\"BY_1_23ui4g23c\"><f n=\"nr\">BY_1_23ui4g23c</f></r><r identifier=\"ORT1_LIZENZ_1\"><f n=\"nr\">ORT1_LIZENZ_1</f></r></result>");
    }

    @Test
    public void RequestLicences_Bundesland_and_Standort_and_Schulnummer_ReturnsBundesland_u_Standort_u_Schulnummer() {
        var arixClient = new ArixClient(baseUrl);
        var licences = arixClient.GetLicences(Bundesland.valueOf("BY"), "ORT1", "f3453b", null).block();

        assertThat(licences.source).isEqualTo("ARIX");
        assertThat(licences.rawResult).isEqualTo("<result><r identifier=\"BY_1_23ui4g23c\"><f n=\"nr\">BY_1_23ui4g23c</f></r><r identifier=\"ORT1_LIZENZ_1\"><f n=\"nr\">ORT1_LIZENZ_1</f></r><r identifier=\"F3453_LIZENZ_1\"><f n=\"nr\">F3453_LIZENZ_1</f></r><r identifier=\"F3453_LIZENZ_2\"><f n=\"nr\">F3453_LIZENZ_2</f></r></result>");
    }

//    @Test
//    public void RequestLicences_notWhitelisted_() {
//        var arixClient = new ArixClient(baseUrlRejecting);
//        var licencesMono = arixClient.GetLicences(Bundesland.valueOf("BY"), "ORT1", "f3453b", null);
//        StepVerifier.create(licencesMono).expectError().verify();
//    }
}
