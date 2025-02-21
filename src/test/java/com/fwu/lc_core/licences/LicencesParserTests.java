package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.UnparsedLicences;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
public class LicencesParserTests {
    @ParameterizedTest
    @MethodSource("provideInvalidInput")
    public void Parser_OnInvalidInput_ReturnsErrorFlux(String source, String rawResult) {
        var unparsedLicences = new UnparsedLicences(source, rawResult);
        var licencesFlux = LicencesParser.parse(unparsedLicences);

        StepVerifier.create(licencesFlux).expectError().verify();
    }

    private static Stream<Arguments> provideInvalidInput() {
        return Stream.of(
                // Common:
                Arguments.of("INVALID_SOURCE", ""),                                                                                 // invalid source
                // Source ARIX:
                Arguments.of("ARIX", "dies ist kein xml"),                                                                          // invalid xml: no root element
                Arguments.of("ARIX", "<kein xml></kein xml>"),                                                                      // invalid xml: space in element
                Arguments.of("ARIX", "<result><FALSCH></result>"),                                                                  // invalid xml: no closing tag
                Arguments.of("ARIX", "<result><r identifier=\"abcde\"><f n=\"nr\">ABCD_EFGH_IJKL</f></r><nonclosingtag></result>"), // invalid xml: no closing tag
                Arguments.of("ARIX", ""),                                                                                           // invalid xml: no root element
                Arguments.of("ARIX", "<result><r identifier=\"abcde\"><f n=\"nr2\">ABCD_EFGH_IJKL</f></r></result>"),               // invalid rawResult: no f with n="nr"
                Arguments.of("ARIX", "<result><r identifier=\"abcde\"><x><f n=\"nr\">ABCD_EFGH_IJKL</f></x></r></result>"),         // invalid rawResult: f with n="nr" not direct child of r
                Arguments.of("ARIX", "<x><result><r identifier=\"abcde\"><f n=\"nr\">ABCD_EFGH_IJKL</f></r></result></x>"),         // invalid rawResult: result not root element
                Arguments.of("ARIX", "<result><r identifier=\"abcde\"><f n=\"nr\"><x>ABCD_EFGH_IJKL</x></f></r></result>"),         // invalid rawResult: f with n="nr" not containing text as first child
                Arguments.of("ARIX", null)                                                                                          // invalid rawResult: null
        );
    }


    @ParameterizedTest
    @MethodSource("provideValidInputAndOutput")
    public void Parser_OnValidInput_ReturnsCorrectOutput(String source, String rawResult, List<String> expectedLicenceCodes) {
        var unparsedLicences = new UnparsedLicences(source, rawResult);
        var licencesFlux = LicencesParser.parse(unparsedLicences);

        var licences = licencesFlux.collectList().block();

        assertThat(licences).isNotNull();

        var licenceCodes = licences.stream().map(l -> l.licenceCode).toList();
        assertThat(licenceCodes).containsExactlyInAnyOrderElementsOf(expectedLicenceCodes);
    }

    private static Stream<Arguments> provideValidInputAndOutput() {
        return Stream.of(
                Arguments.of("ARIX", "<result></result>", List.of()),
                Arguments.of("ARIX", "<result><r identifier=\"abcde\"><f n=\"nr\">ABCD_EFGH_IJKL</f></r></result>", List.of("ABCD_EFGH_IJKL")),
                Arguments.of("ARIX", "<result><r identifier=\"abcde\"><f n=\"nr\">lizenzcode1</f></r><r identifier=\"abcde\"><f n=\"nr\">lizenzcode2</f></r></result>", List.of("lizenzcode1", "lizenzcode2")),
                Arguments.of("ARIX", "<result><zusaetzlichertag/><r identifier=\"abcde\"><f n=\"nr\">ABCD_EFGH_IJKL</f><f n=\"zusaetzliches feld\">info</f><zusatz>abc</zusatz></r></result>", List.of("ABCD_EFGH_IJKL")),
                Arguments.of("ARIX", "   <result   >   <r identifier   =  \"abcde\">      \n\n      <f      n=\"nr\">ABCD EFGH IJKL</f>\n       </r></result>", List.of("ABCD EFGH IJKL"))
        );
    }
}
