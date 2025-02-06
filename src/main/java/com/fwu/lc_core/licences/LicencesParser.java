package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.Licence;
import com.fwu.lc_core.licences.models.UnparsedLicences;
import reactor.core.publisher.Flux;

public class LicencesParser {
    public static Flux<Licence> parse(UnparsedLicences unparsedLicences) {
        return Flux.empty();
    }
}
