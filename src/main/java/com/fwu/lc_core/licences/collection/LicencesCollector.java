package com.fwu.lc_core.licences.collection;

import org.springframework.beans.factory.annotation.Value;

import com.fwu.lc_core.licences.collection.arix.ArixClient;
import com.fwu.lc_core.licences.models.LicencesRequestDto;
import com.fwu.lc_core.licences.models.UnparsedLicences;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class LicencesCollector {

    public LicencesCollector(@Value("${mocks.arix.accepting.url}") String arixUrl) {
        LicencesCollector.arixUrl = arixUrl;
    }

    private static String arixUrl;

    public static Flux<UnparsedLicences> getUnparsedLicences(LicencesRequestDto params) {
        var arixClient = new ArixClient(arixUrl);
        var arixUnparsedLicences = arixClient.getLicences(params.bundesland(), params.standortnummer(), params.schulnummer(), params.userId());

        return Flux.merge(arixUnparsedLicences);
    }
}

