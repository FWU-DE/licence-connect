package com.fwu.lc_core.licences.collection.arix;

import com.fwu.lc_core.shared.Bundesland;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ArixClientTests {
    @Value("${mocks.arix.accepting.url}")
    private String baseUrlAccepting;
    @Value("${mocks.arix.rejecting.url}")
    private String baseUrlRejecting;

    @Test
    public void RequestLicences_BundeslandOnly_ReturnsLicencesWhichAreIssuedToEverybodyOfTheBundesland() {
        var arixClient = new ArixClient(baseUrlAccepting);
        var licences = arixClient.getLicences(Bundesland.valueOf("BY"), null, null, null).block();

        assertThat(licences.source).isEqualTo("ARIX");
        assertThat(licences.rawResult).isEqualTo("<result><r identifier=\"3\"><f n=\"nr\">BY_1_23ui4g23c</f></r></result>");
    }

    @Test
    public void RequestLicences_Bundesland_and_Standort_ReturnsBundesland_u_Standort() {
        var arixClient = new ArixClient(baseUrlAccepting);
        var licences = arixClient.getLicences(Bundesland.valueOf("BY"), "ORT1", null, null).block();

        assertThat(licences.source).isEqualTo("ARIX");
        assertThat(licences.rawResult).isEqualTo("<result><r identifier=\"3\"><f n=\"nr\">BY_1_23ui4g23c</f></r><r identifier=\"4\"><f n=\"nr\">ORT1_LIZENZ_1</f></r></result>");
    }

    @Test
    public void RequestLicences_Bundesland_and_Standort_and_Schulnummer_ReturnsBundesland_u_Standort_u_Schulnummer() {
        var arixClient = new ArixClient(baseUrlAccepting);
        var licences = arixClient.getLicences(Bundesland.valueOf("BY"), "ORT1", "f3453b", null).block();

        assertThat(licences.source).isEqualTo("ARIX");
        assertThat(licences.rawResult).isEqualTo("<result><r identifier=\"3\"><f n=\"nr\">BY_1_23ui4g23c</f></r><r identifier=\"4\"><f n=\"nr\">ORT1_LIZENZ_1</f></r><r identifier=\"7\"><f n=\"nr\">F3453_LIZENZ_1</f></r><r identifier=\"8\"><f n=\"nr\">F3453_LIZENZ_2</f></r></result>");
    }

    @ParameterizedTest
    @MethodSource("provideIncorrectInfo")
    public void RequestLicences_ArgumentsAreNotANonemptyPrefix(Bundesland bundesland, String standortnummer, String schulnummer, String userId) {
        var arixClient = new ArixClient(baseUrlAccepting);
        var licencesMono = arixClient.getLicences(bundesland, standortnummer, schulnummer, userId);
        StepVerifier.create(licencesMono).expectError().verify();
    }

    @Test
    public void RequestLicences_notWhitelisted_throwsError() {
        var arixClient = new ArixClient(baseUrlRejecting);
        var licencesMono = arixClient.getLicences(Bundesland.valueOf("BY"), "ORT1", null, null);
        StepVerifier.create(licencesMono).expectError().verify();
    }

    private static Stream<Arguments> provideIncorrectInfo() {
        return Stream.of(
                Arguments.of("BY", "Std", null, "userId"),
                Arguments.of("BY", null, "Schule", "userId"),
                Arguments.of(null, "Std", "Schule", "userId"),
                Arguments.of("BY", null, "Schule", "userId"),
                Arguments.of("BY", null, "Schule", "userId"),
                Arguments.of("BY", null, null, "userId"),
                Arguments.of("BY", null, "Schule", null),
                Arguments.of(null, null, null, null)
        );
    }
}
