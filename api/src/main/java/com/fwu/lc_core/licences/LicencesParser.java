package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.ODRLLicenceResponse;
import com.fwu.lc_core.licences.models.UnparsedLicences;
import reactor.core.publisher.Mono;

public class LicencesParser {
    public static Mono<ODRLLicenceResponse> parse(UnparsedLicences unparsedLicences) {
        return switch (unparsedLicences.source) {
            case ARIX -> LicencesParserArix.parse(unparsedLicences);
            case LC_HALT -> LicencesParserLCHalt.parse(unparsedLicences);
            default -> Mono.error(new Exception("Exception in LicenceParser: Unknown source: " + unparsedLicences.source));
        };
    }
}
