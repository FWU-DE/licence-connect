package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.LicenceHolder;
import com.fwu.lc_core.licences.models.LicenceResponse;
import com.fwu.lc_core.licences.models.UnparsedLicences;
import reactor.core.publisher.Mono;

public class LicencesParser {
    public static Mono<LicenceResponse> parse(UnparsedLicences unparsedLicences) {
        if (LicenceHolder.ARIX.equals(unparsedLicences.source)) {
            return Mono.defer(() -> LicencesParserArix.parse(unparsedLicences));
        } else {
            return Mono.error(new Exception("Exception in LicenceParser: Unknown source: " + unparsedLicences.source));
        }
    }
}
