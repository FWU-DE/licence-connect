package com.fwu.lc_core.licences.collection;

import com.fwu.lc_core.licences.models.LicencesRequestDto;
import com.fwu.lc_core.shared.Bundesland;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.AvailableLicenceHolders;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.ClientLicenseHolderFilterService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;

import com.fwu.lc_core.licences.collection.arix.ArixClient;
import com.fwu.lc_core.licences.models.UnparsedLicences;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class LicencesCollector {

    private static ClientLicenseHolderFilterService clientLicenseHolderFilterService;

    public LicencesCollector(ClientLicenseHolderFilterService clientLicenseHolderFilterService, @Value("${mocks.arix.accepting.url}") String arixUrl) {
        LicencesCollector.clientLicenseHolderFilterService = clientLicenseHolderFilterService;
        LicencesCollector.arixUrl = arixUrl;
    }

    private static String arixUrl;

    public static Flux<UnparsedLicences> getUnparsedLicences(LicencesRequestDto params, String clientName) {
        if (!clientLicenseHolderFilterService.getAllowedLicenceHolders(clientName).contains(AvailableLicenceHolders.ARIX))
            return Flux.empty();
        var arixClient = new ArixClient(arixUrl);
        var arixUnparsedLicences = arixClient.getLicences(params.bundesland(), params.standortnummer(), params.schulnummer(), params.userId());
        return Flux.merge(arixUnparsedLicences);
    }
}

