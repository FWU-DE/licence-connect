package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.LicenceHolder;
import com.fwu.lc_core.licences.models.ODRLlicenceResponse;
import com.fwu.lc_core.licences.models.UnparsedLicences;
import reactor.core.publisher.Mono;

public class LicencesParser {
    public static Mono<ODRLlicenceResponse> parse(UnparsedLicences unparsedLicences) {
        if (unparsedLicences.source.equals(LicenceHolder.ARIX)) {
            return LicencesParserArix.parse(unparsedLicences);
        }
        return Mono.error(new Exception("Exception in LicenceParser: Unknown source: " + unparsedLicences.source));
    }
}
