package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.LicenceHolder;
import com.fwu.lc_core.licences.models.LicenceResponse;
import com.fwu.lc_core.licences.models.UnparsedLicences;
import reactor.core.publisher.Flux;

public class LicencesParser {
    public static Flux<LicenceResponse> parse(UnparsedLicences unparsedLicences) {
        if (LicenceHolder.ARIX.equals(unparsedLicences.source)) {
            return Flux.defer(() -> LicencesParserArix.parse(unparsedLicences));
        } else {
            return Flux.error(new Exception("Exception in LicenceParser: Unknown source: " + unparsedLicences.source));
        }
    }
}
