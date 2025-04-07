package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.LicenceResponse;
import com.fwu.lc_core.licences.models.UnparsedLicences;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Stream;

import static com.fwu.lc_core.licences.TestHelper.extractLicenceCodesFrom;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
public class LicencesParserArixTests {

    private static final String expectedAssignee = "<VIDIS-ID>";
    private static final String expectedAssigner = "<ARIX>";
    // This should vary in reality but Arix does not specify any action at all.
    private static final String expectedAction = "play";


    @ParameterizedTest
    @MethodSource("provideInvalidInput")
    public void Parser_OnInvalidInput_ReturnsErrorFlux(String source, String rawResult) {
        var unparsedLicences = new UnparsedLicences(source, rawResult);
        var licencesFlux = LicencesParser.parse(unparsedLicences);

        StepVerifier.create(licencesFlux).expectError().verify();
    }


    @ParameterizedTest
    @MethodSource("provideValidInputAndOutput")
    public void Parser_OnValidInput_ReturnsCorrectOutput(String source, String rawResult, List<String> expectedLicenceCodes) {
        var unparsedLicences = new UnparsedLicences(source, rawResult);
        var licencesFlux = LicencesParser.parse(unparsedLicences);

        var licences = licencesFlux.collectList().block();

        assertThat(licences).isNotNull();
        var licenceCodes = extractLicenceCodesFrom(licences);
        assertThat(licenceCodes).containsExactlyInAnyOrderElementsOf(expectedLicenceCodes);
    }

    @ParameterizedTest
    @MethodSource("provideValidInputAndOutput")
    public void Parse_ValidInput_ReturnsCorrectLicenceFormat(String source, String rawResult, List<String> expectedLicenceCodes) {
        var unparsedLicences = new UnparsedLicences(source, rawResult);
        var licencesFlux = LicencesParser.parse(unparsedLicences);

        StepVerifier.Step<LicenceResponse> verifier = StepVerifier.create(licencesFlux);

        for (final String expectedTarget : expectedLicenceCodes) {
            verifier = verifier.assertNext(licence -> {
                assertThat(licence.context).isEqualTo("http://www.w3.org/ns/odrl.jsonld");
                assertThat(licence.type).isEqualTo("Set");
                assertThat(licence.uid).isEqualTo("https://w3c.github.io/odrl/bp/examples/2");
                assertThat(licence.permission).hasSize(1);
                var permission = licence.permission.getFirst();
                assertThat(permission.target).isEqualTo(expectedTarget);
                assertThat(permission.assignee).isEqualTo(expectedAssignee);
                assertThat(permission.assigner).isEqualTo(expectedAssigner);
                assertThat(permission.action).isEqualTo(expectedAction);
            });
        }
        verifier.verifyComplete();
    }

    private static Stream<Arguments> provideValidInputAndOutput() {
        return Stream.of(
                Arguments.of("ARIX", "<result></result>", List.of()),
                Arguments.of("ARIX", "<result><r identifier=\"ABCD_EFGH_IJKL\"><f n=\"nr\">abcde</f></r></result>", List.of("ABCD_EFGH_IJKL")),
                Arguments.of("ARIX", "<result><r identifier=\"ABCD_EFGH_IJKL\"></r></result>", List.of("ABCD_EFGH_IJKL")),
                Arguments.of("ARIX", "<result><r identifier=\"lizenzcode1\"><f n=\"nr\">abcde</f></r><r identifier=\"lizenzcode2\"><f n=\"nr\">abcde</f></r></result>", List.of("lizenzcode1", "lizenzcode2")),
                Arguments.of("ARIX", "<result><zusaetzlichertag/><r identifier=\"ABCD_EFGH_IJKL\"><f n=\"nr\">abcde</f><f n=\"zusaetzliches feld\">info</f><zusatz>abc</zusatz></r></result>", List.of("ABCD_EFGH_IJKL")),
                Arguments.of("ARIX", "   <result   >   <r identifier   =  \"ABCD EFGH IJKL\">      \n\n      <f      n=\"nr\">abcde</f>\n       </r></result>", List.of("ABCD EFGH IJKL"))
        );
    }

    private static Stream<Arguments> provideInvalidInput() {
        return Stream.of(
                Arguments.of("INVALID_SOURCE", ""),                                                                                 // invalid source
                Arguments.of("ARIX", "dies ist kein xml"),                                                                          // invalid xml: no root element
                Arguments.of("ARIX", "<kein xml></kein xml>"),                                                                      // invalid xml: space in element
                Arguments.of("ARIX", "<result><FALSCH></result>"),                                                                  // invalid xml: no closing tag
                Arguments.of("ARIX", "<result><r identifier=\"abcde\"><f n=\"nr\">ABCD_EFGH_IJKL</f></r><nonclosingtag></result>"), // invalid xml: no closing tag
                Arguments.of("ARIX", ""),                                                                                           // invalid xml: no root element
                Arguments.of("ARIX", "<result><r><f n=\"nr2\">ABCD_EFGH_IJKL</f></r></result>"),                                    // invalid rawResult: no identifier
                Arguments.of("ARIX", "<x><result><r identifier=\"abcde\"><f n=\"nr\">ABCD_EFGH_IJKL</f></r></result></x>"),         // invalid rawResult: result not root element
                Arguments.of("ARIX", null)                                                                                          // invalid rawResult: null
        );
    }
}
