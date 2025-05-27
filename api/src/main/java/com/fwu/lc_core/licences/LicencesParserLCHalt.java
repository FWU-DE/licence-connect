package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.ODRLLicenceResponse;
import com.fwu.lc_core.licences.models.UnparsedLicences;
import reactor.core.publisher.Mono;

public class LicencesParserLCHalt {
    public static Mono<ODRLLicenceResponse> parse(UnparsedLicences unparsedLicences) {
        return Mono.empty();
    }
}
