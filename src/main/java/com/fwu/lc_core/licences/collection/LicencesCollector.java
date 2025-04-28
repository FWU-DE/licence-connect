package com.fwu.lc_core.licences.collection;

import com.fwu.lc_core.licences.collection.arix.ArixClient;
import com.fwu.lc_core.licences.models.LicencesRequestDto;
import com.fwu.lc_core.licences.models.UnparsedLicences;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.AvailableLicenceHolders;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.ClientLicenseHolderFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class LicencesCollector {
    @Autowired
    private ClientLicenseHolderFilterService clientLicenseHolderFilterService;
    private final String arixUrl;

    public LicencesCollector(@Value("${arix.accepting.url}") String arixUrl) {
        this.arixUrl = arixUrl;
    }

    public Mono<UnparsedLicences> getUnparsedLicences(LicencesRequestDto params, String clientName) {
        if (!clientLicenseHolderFilterService.getAllowedLicenceHolders(clientName).contains(AvailableLicenceHolders.ARIX))
            return Mono.empty();
        var arixClient = new ArixClient(arixUrl);
        return arixClient.getLicences(params.bundesland(), params.standortnummer(), params.schulnummer(), params.userId());
    }
}

