package com.fwu.lc_core.licences.collection.arix;

import com.fwu.lc_core.licences.models.LicenceHolder;
import com.fwu.lc_core.licences.models.UnparsedLicences;
import com.fwu.lc_core.shared.Bundesland;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import reactor.test.StepVerifier;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
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
    public void RequestLicences_GivenCorrectBL_Yields_Result() throws ParserConfigurationException, IOException, SAXException {
        ArixClient arixClient = new ArixClient(baseUrlAccepting);
        UnparsedLicences licences = arixClient.getLicences(Bundesland.valueOf("STK"), null, null, null).block();

        assert licences != null;
        assertThat(licences.source).isEqualTo(LicenceHolder.ARIX);
        assertValidityOfValidArixResponseBody(licences);
    }

    @Test
    public void RequestLicences_GivenCorrectBLandStandort_Yields_Result() throws ParserConfigurationException, IOException, SAXException {
        ArixClient arixClient = new ArixClient(baseUrlAccepting);
        UnparsedLicences licences = arixClient.getLicences(Bundesland.valueOf("STK"), "STR", null, null).block();

        assert licences != null;
        assertThat(licences.source).isEqualTo(LicenceHolder.ARIX);
        assertValidityOfValidArixResponseBody(licences);
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

    private static void assertValidityOfValidArixResponseBody(UnparsedLicences licences) throws SAXException, IOException, ParserConfigurationException {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(licences.rawResult.getBytes()));

        Element rootElement = document.getDocumentElement();
        assertThat(rootElement.getTagName()).isEqualTo("result");

        NodeList rNodes = rootElement.getElementsByTagName("r");
        assertThat(rNodes.getLength()).isGreaterThan(0);
        for (int i = 0; i < rNodes.getLength(); i++) {
            assertThat(rNodes.item(i).getAttributes().getNamedItem("identifier")).isNotNull();
            assertThat(rNodes.item(i).getAttributes().getNamedItem("identifier").getNodeValue()).isNotEmpty();
        }
    }
}
