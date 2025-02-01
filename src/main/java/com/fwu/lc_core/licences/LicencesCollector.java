package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.LicencesRequestParams;
import com.fwu.lc_core.licences.models.UnparsedLicences;
import reactor.core.publisher.Flux;

public class LicencesCollector {
    public static Flux<UnparsedLicences> getUnparsedLicences(LicencesRequestParams params) {
        // For collecting licences from the endpoint, use the WebClient from spring-boot-starter-webflux, which produces
        // a Mono<T>. Arbitrarily many Mono<UnparsedLicences> can be merged into a Flux<UnparsedLicences> and then
        // returned here.
        return Flux.empty();
    }
}
