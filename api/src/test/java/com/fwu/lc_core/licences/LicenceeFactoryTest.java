package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.Licencee;
import com.fwu.lc_core.shared.Bundesland;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class LicenceeFactoryTest {

    @Autowired
    private LicenceeFactory licenceeFactory;

    @ParameterizedTest
    @CsvSource(value = {
            "BY, 12345, user1, null",
            "BB, 12-34-567890, user1, DISTRICT_1",
            "BB, 12-34-999111, user1, null",
            "null, 12345, user1, null"
    }, nullValues = "null")
    void create_valid_licencees(String bundesland, String schulnummer, String userId, String expectedStandort) {
        Licencee licencee = licenceeFactory.create(bundesland, null,  schulnummer, userId, "bildungsmediatken-bbmv-o");

        assertLicencee(licencee, bundesland, expectedStandort, schulnummer, userId);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "BB, INVALID, user1, Invalid Schulkennung format for BB",
            "BB, null, user1, Schulkennung must be provided for BB bundesland",
            "INVALID, 12345, user1, INVALID"
    }, nullValues = "null")
    void create_throws_exception_for_invalid_input(String bundesland, String schulnummer, String userId, String expectedMessagePart) {
        assertThatThrownBy(() -> licenceeFactory.create(bundesland, null, schulnummer, userId, "bildungsmediatken-bbmv-o"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(expectedMessagePart);
    }

    @Test
    void check_schulnummer_standortnummer_mapping_disabled_for_other_clients() {
        String bundesland = "BB";
        String schulnummer = "12-34-567890";
        String userId = "user1";
        String clientId = "some-other-client";

        Licencee licencee = licenceeFactory.create(bundesland, null, schulnummer, userId, clientId);

        assertLicencee(licencee, bundesland, null, schulnummer, userId);
    }

    private void assertLicencee(Licencee actual, String expectedBundeslandStr, String expectedStandort, String expectedSchulnummer, String expectedUserId) {
        Bundesland expectedBundesland = null;
        if (expectedBundeslandStr != null) {
            expectedBundesland = Bundesland.fromAbbreviation(expectedBundeslandStr);
        }

        assertThat(actual.bundesland()).isEqualTo(expectedBundesland);
        assertThat(actual.standortnummer()).isEqualTo(expectedStandort);
        assertThat(actual.schulnummer()).isEqualTo(expectedSchulnummer);
        assertThat(actual.userId()).isEqualTo(expectedUserId);
    }
}
