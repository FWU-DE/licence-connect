package com.fwu.lc_core.licences;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fwu.lc_core.licences.models.Licencee;
import com.fwu.lc_core.shared.Bundesland;

@SpringBootTest
class LicenceeFactoryTest {

    @Autowired
    private LicenceeFactory licenceeFactory;

    @ParameterizedTest
    @CsvSource(value = {
            "BY, 12345, null",
            "BB, DE-BB-567890, DISTRICT_1",
            "BY, DE-BY-199999, null"
    }, nullValues = "null")
    void create_valid_licencees(String bundesland, String schulnummer, String expectedStandort) {
        Licencee licencee = licenceeFactory.create(bundesland, null,  schulnummer, "bildungsmediatken-bbmv-o");

        assertLicencee(licencee, bundesland, expectedStandort, schulnummer);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "BB, INVALID, Invalid Schulkennung or schulnummer format",
            "INVALID, 12345, INVALID",
            "null, 12345, Abbreviation cannot be null"
    }, nullValues = "null")
    void create_throws_exception_for_invalid_input(String bundesland, String schulnummer, String expectedMessagePart) {
        assertThatThrownBy(() -> licenceeFactory.create(bundesland, null, schulnummer, "bildungsmediatken-bbmv-o"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(expectedMessagePart);
    }

    @Test
    void check_schulnummer_standortnummer_mapping_disabled_for_other_clients() {
        String bundesland = "BB";
        String schulnummer = "12-34-567890";
        String clientId = "some-other-client";

        Licencee licencee = licenceeFactory.create(bundesland, null, schulnummer, clientId);

        assertLicencee(licencee, bundesland, null, schulnummer);
    }

    private void assertLicencee(Licencee actual, String expectedBundeslandStr, String expectedStandort, String expectedSchulnummer) {
        Bundesland expectedBundesland = null;
        if (expectedBundeslandStr != null) {
            expectedBundesland = Bundesland.fromAbbreviation(expectedBundeslandStr);
        }

        assertThat(actual.bundesland()).isEqualTo(expectedBundesland);
        assertThat(actual.standortnummer()).isEqualTo(expectedStandort);
        assertThat(actual.schulnummer()).isEqualTo(expectedSchulnummer);
    }
}
