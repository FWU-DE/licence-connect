package com.fwu.lc_core.licences.clients.arix;

import com.fwu.lc_core.shared.Bundesland;
import com.fwu.lc_core.shared.LicenceHolder;
import com.fwu.lc_core.licences.models.ODRLPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;
import reactor.test.StepVerifier;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ArixClientTests {
    @Value("${arix.url}")
    private String baseUrlAccepting;
    @Value("${arix.rejecting.url}")
    private String baseUrlRejecting;

    @ParameterizedTest
    @MethodSource("provideCorrectInput")
    public void RequestPermissions_GivenCorrectInput_Yields_Result(Bundesland bundesland, String standortnummer, String schulnummer, String userId) throws ParserConfigurationException, IOException, SAXException {
        ArixClient arixClient = new ArixClient(baseUrlAccepting, 1000);
        var permissions = arixClient.getPermissions(bundesland, standortnummer, schulnummer, userId).block();

        assertThat(permissions).isNotNull();
        for (ODRLPolicy.Permission p : permissions) {
            assertThat(p.assigner).isEqualTo(LicenceHolder.ARIX);
        }
    }


    @ParameterizedTest
    @MethodSource("provideIncorrectInfo")
    public void RequestLicences_ArgumentsAreNotANonemptyPrefix(Bundesland bundesland, String standortnummer, String schulnummer, String userId) {
        var arixClient = new ArixClient(baseUrlAccepting, 1000);
        var permissionsMono = arixClient.getPermissions(bundesland, standortnummer, schulnummer, userId);
        StepVerifier.create(permissionsMono).expectError().verify();
    }

    @Test
    public void RequestLicences_notWhitelisted_throwsError() {
        var arixClient = new ArixClient(baseUrlRejecting, 1000);
        var permissionsMono = arixClient.getPermissions(Bundesland.BY, "ORT1", null, null);
        StepVerifier.create(permissionsMono).expectError().verify();
    }

    @Test
    public void Constructor_SetsSearchLimit_Correctly() {
        int expectedLimit = 9999;
        var arixClient = new ArixClient(baseUrlAccepting, expectedLimit);
        
        assertThat(arixClient).hasFieldOrPropertyWithValue("searchLimit", expectedLimit);
    }

    private static Stream<Arguments> provideIncorrectInfo() {
        return Stream.of(
                Arguments.of(null, "Std", "Schule", "userId"),
                Arguments.of(null, null, null, null)
        );
    }
    private static Stream<Arguments> provideCorrectInput() {
        return Stream.of(
                Arguments.of(Bundesland.STK, "STR", null, null),
                Arguments.of(Bundesland.BY, null, "Schule", "userId"),
                Arguments.of(Bundesland.BY, null, "Schule", "userId"),
                Arguments.of(Bundesland.BY, "Std", null, "userId"),
                Arguments.of(Bundesland.BY, null, "Schule", "userId"),
                Arguments.of(Bundesland.BY, null, null, "userId"),
                Arguments.of(Bundesland.BY, null, "Schule", null),
                Arguments.of(Bundesland.STK, null, null, null)
        );
    }
}
