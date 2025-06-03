package com.fwu.lc_core.licences.clients.arix;

import com.fwu.lc_core.licences.models.ODRLPolicy;
import com.fwu.lc_core.shared.LicenceHolder;
import com.fwu.lc_core.licences.models.ODRLAction;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import java.util.List;
import java.util.stream.Stream;

import static com.fwu.lc_core.licences.TestHelper.extractLicenceCodesFrom;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class ArixParserTests {
    private static final ODRLAction EXPECTED_ACTION = ODRLAction.Use;
    private static final LicenceHolder EXPECTED_ASSIGNER = LicenceHolder.ARIX;


    @ParameterizedTest
    @MethodSource("provideInvalidInput")
    public void Parser_OnInvalidInput_ReturnsError(String rawResult) {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            var permissions = ArixParser.parse(rawResult);
        });

        String expectedMessage = "Error extracting licence codes: ";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }


    @ParameterizedTest
    @MethodSource("provideValidInputAndOutput")
    public void Parser_OnValidInput_ReturnsCorrectOutput(String rawResult, List<String> expectedLicenceCodes) {
        try {
            var permissions = ArixParser.parse(rawResult);

            assertThat(permissions).isNotNull();
            assertEquals(permissions.size(), expectedLicenceCodes.size());

            List<String> licenceCodes = extractLicenceCodesFrom(permissions);
            assertThat(licenceCodes).containsExactlyInAnyOrderElementsOf(expectedLicenceCodes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest
    @MethodSource("provideValidInputAndOutput")
    public void Parse_ValidInput_ReturnsCorrectLicenceFormat(String rawResult, List<String> expectedLicenceCodes) {
        try {
            var permissions = ArixParser.parse(rawResult);

            assertThatCorrectLicenceResponseCreated(expectedLicenceCodes, permissions);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void assertThatCorrectLicenceResponseCreated(List<String> expectedLicenceCodes, List<ODRLPolicy.Permission> permissions) {
        assertThat(permissions).isNotNull();
        assertThat(permissions.size()).isEqualTo(expectedLicenceCodes.size());
        if (permissions.isEmpty()) {
            return;
        }
        for (final String expectedTarget : expectedLicenceCodes) {
            var permission = permissions.stream().filter(p -> p.target.equals(expectedTarget)).findFirst().orElse(null);
            assertThatTargetsAllowUseIssuedByArixAndMatchTarget(expectedTarget, permission);
        }
    }

    private static void assertThatTargetsAllowUseIssuedByArixAndMatchTarget(String expectedTarget, ODRLPolicy.Permission permission) {
        assertThat(permission).isNotNull();
        assertThat(permission.target).isEqualTo(expectedTarget);
        assertThat(permission.assigner).isEqualTo(EXPECTED_ASSIGNER);
        assertThat(permission.action).isEqualTo(EXPECTED_ACTION);
    }

    private static void assertLicenceResponseMetaData(ODRLPolicy licence) {
        assertThat(licence.context).isEqualTo("http://www.w3.org/ns/odrl.jsonld");
        assertThat(licence.type).isEqualTo("Set");
        assertThatUrnIsValid(licence.uid);
    }

    private static void assertThatUrnIsValid(String uid) {
        assertThatCode(() -> UUID.fromString(uid.replace("urn:uuid:", ""))).doesNotThrowAnyException();
    }


    private static Stream<Arguments> provideValidInputAndOutput() {
        return Stream.of(
                Arguments.of("<result></result>", List.of()),
                Arguments.of("<result><r identifier=\"ABCD_EFGH_IJKL\"><f n=\"nr\">abcde</f></r></result>", List.of("ABCD_EFGH_IJKL")),
                Arguments.of("<result><r identifier=\"ABCD_EFGH_IJKL\"></r></result>", List.of("ABCD_EFGH_IJKL")),
                Arguments.of("<result><r identifier=\"lizenzcode1\"><f n=\"nr\">abcde</f></r><r identifier=\"lizenzcode2\"><f n=\"nr\">abcde</f></r></result>", List.of("lizenzcode1", "lizenzcode2")),
                Arguments.of("<result><zusaetzlichertag/><r identifier=\"ABCD_EFGH_IJKL\"><f n=\"nr\">abcde</f><f n=\"zusaetzliches feld\">info</f><zusatz>abc</zusatz></r></result>", List.of("ABCD_EFGH_IJKL")),
                Arguments.of("   <result   >   <r identifier   =  \"ABCD EFGH IJKL\">      \n\n      <f      n=\"nr\">abcde</f>\n       </r></result>", List.of("ABCD EFGH IJKL"))
        );
    }

    private static Stream<Arguments> provideInvalidInput() {
        String nullString = null;
        return Stream.of(
                Arguments.of("dies ist kein xml"),                                                                          // invalid xml: no root element
                Arguments.of("<kein xml></kein xml>"),                                                                      // invalid xml: space in element
                Arguments.of("<result><FALSCH></result>"),                                                                  // invalid xml: no closing tag
                Arguments.of("<result><r identifier=\"abcde\"><f n=\"nr\">ABCD_EFGH_IJKL</f></r><nonclosingtag></result>"), // invalid xml: no closing tag
                Arguments.of(""),                                                                                           // invalid xml: no root element
                Arguments.of("<result><r><f n=\"nr2\">ABCD_EFGH_IJKL</f></r></result>"),                                    // invalid rawResult: no identifier
                Arguments.of("<x><result><r identifier=\"abcde\"><f n=\"nr\">ABCD_EFGH_IJKL</f></r></result></x>"),         // invalid rawResult: result not root element
                Arguments.of(nullString)                                                                                    // invalid rawResult: null
        );
    }
}

