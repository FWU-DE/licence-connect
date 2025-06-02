package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.clients.ArixClient;
import com.fwu.lc_core.shared.LicenceHolder;
import com.fwu.lc_core.licences.models.ODRLPolicy;
import com.fwu.lc_core.shared.Bundesland;
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
    @Value("${arix.accepting.url}")
    private String baseUrlAccepting;
    @Value("${arix.rejecting.url}")
    private String baseUrlRejecting;

    @Test
    public void RequestPermissions_GivenCorrectBundesland_Yields_Result() throws ParserConfigurationException, IOException, SAXException {
        ArixClient arixClient = new ArixClient(baseUrlAccepting);
        var permissions = arixClient.getPermissions(Bundesland.valueOf("STK"), null, null, null).block();

        assertThat(permissions).isNotNull();
        for (ODRLPolicy.Permission p : permissions) {
            assertThat(p.assigner).isEqualTo(LicenceHolder.ARIX);
        }
    }

    @Test
    public void RequestPermissions_GivenCorrectBundeslandAndStandort_Yields_Result() throws ParserConfigurationException, IOException, SAXException {
        ArixClient arixClient = new ArixClient(baseUrlAccepting);
        var permissions = arixClient.getPermissions(Bundesland.valueOf("STK"), "STR", null, null).block();

        assertThat(permissions).isNotNull();
        for (ODRLPolicy.Permission p : permissions) {
            assertThat(p.assigner).isEqualTo(LicenceHolder.ARIX);
        }
    }

    @ParameterizedTest
    @MethodSource("provideIncorrectInfo")
    public void RequestLicences_ArgumentsAreNotANonemptyPrefix(Bundesland bundesland, String standortnummer, String schulnummer, String userId) {
        var arixClient = new ArixClient(baseUrlAccepting);
        var permissionsMono = arixClient.getPermissions(bundesland, standortnummer, schulnummer, userId);
        StepVerifier.create(permissionsMono).expectError().verify();
    }

    @Test
    public void RequestLicences_notWhitelisted_throwsError() {
        var arixClient = new ArixClient(baseUrlRejecting);
        var permissionsMono = arixClient.getPermissions(Bundesland.valueOf("BY"), "ORT1", null, null);
        StepVerifier.create(permissionsMono).expectError().verify();
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
