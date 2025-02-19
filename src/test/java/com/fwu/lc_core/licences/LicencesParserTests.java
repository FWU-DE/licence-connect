package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.UnparsedLicences;
import com.fwu.lc_core.licences.models.Licence;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
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

    private static Stream<Arguments> provideInvalidInput(){
        return Stream.of(
                Arguments.of("INVALID_SOURCE", ""),
                Arguments.of("ARIX", "dies ist kein xml"),
                Arguments.of("ARIX", "<kein xml></kein xml>"),
                Arguments.of("ARIX", "<result><FALSCH></result>"),
                Arguments.of("ARIX", "<result><r identifier=\"abcde\"><f n=\"nr\">ABCD_EFGH_IJKL</f></r><nonclosingtag></result>"),
                Arguments.of("ARIX", ""),
                Arguments.of("ARIX", null)
        );
    }


    @ParameterizedTest
    @MethodSource("provideValidInputAndOutput")
    public void Parser_OnValidInput_ReturnsCorrectOutput(String source, String rawResult, List<String> expectedLicenceCodes) {
        var unparsedLicences = new UnparsedLicences(source, rawResult);
        var licencesFlux = LicencesParser.parse(unparsedLicences);

        var licences = licencesFlux.collectList().block();

        assertThat(licences).isNotNull();

        var licenceCodes = licences.stream().map(l->l.licenceCode).toList();
        assertThat(licenceCodes).containsExactlyInAnyOrderElementsOf(expectedLicenceCodes);
    }

    private static Stream<Arguments> provideValidInputAndOutput(){
        return Stream.of(
                Arguments.of("ARIX", "<result></result>", List.of()),
                Arguments.of("ARIX", "<result><r identifier=\"abcde\"><f n=\"nr\">ABCD_EFGH_IJKL</f></r></result>", List.of("ABCD_EFGH_IJKL")),
                Arguments.of("ARIX", "<result><r identifier=\"abcde\"><f n=\"nr\">lizenzcode1</f></r><r identifier=\"abcde\"><f n=\"nr\">lizenzcode2</f></r></result>", List.of("lizenzcode1", "lizenzcode2")),
                Arguments.of("ARIX", "<result><zusaetzlichertag/><r identifier=\"abcde\"><f n=\"nr\">ABCD_EFGH_IJKL</f><f n=\"zusaetzliches feld\">info</f><zusatz>abc</zusatz></r></result>", List.of("ABCD_EFGH_IJKL")),
                Arguments.of("ARIX", "   <result   >   <r identifier   =  \"abcde\">      \n\n      <f      n=\"nr\">ABCD EFGH IJKL</f>\n       </r></result>", List.of("ABCD EFGH IJKL"))
        );
    }
}
