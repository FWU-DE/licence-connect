package com.fwu.lc_core.licences.collection;

import com.fwu.lc_core.licences.models.LicencesRequestDto;
import com.fwu.lc_core.licences.models.UnparsedLicences;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.AvailableLicenceHolders;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.ClientLicenseHolderFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.EnumSet;

@Component
public class LicencesCollector {
    @Autowired
    private ClientLicenseHolderFilterService clientLicenseHolderFilterService;

    @Autowired
    private LCHaltClient lcHaltClient;

    private final String arixUrl;

    public LicencesCollector(@Value("${arix.accepting.url}") String arixUrl) {
        this.arixUrl = arixUrl;
    }

    public Flux<UnparsedLicences> getUnparsedLicences(LicencesRequestDto params, String clientName) {
        var allowedLicenceHolders = clientLicenseHolderFilterService.getAllowedLicenceHolders(clientName);
        var arixLicences = getUnparsedArixLicences(params, allowedLicenceHolders);
        var lcHaltLicences = getUnparsedLCHaltLicences(params, allowedLicenceHolders);
        return Flux.merge(arixLicences, lcHaltLicences);
    }

    private Mono<UnparsedLicences> getUnparsedArixLicences(LicencesRequestDto params, EnumSet<AvailableLicenceHolders> availableLicenceHolders) {
        if (!availableLicenceHolders.contains(AvailableLicenceHolders.ARIX))
            return Mono.empty();

        var arixClient = new ArixClient(arixUrl);
        return arixClient.getLicences(params.bundesland(), params.standortnummer(), params.schulnummer(), params.userId());
    }

    private Mono<UnparsedLicences> getUnparsedLCHaltLicences(LicencesRequestDto params, EnumSet<AvailableLicenceHolders> availableLicenceHolders) {
        if (!availableLicenceHolders.contains(AvailableLicenceHolders.LC_HALT))
            return Mono.empty();
        
        return lcHaltClient.getLicences(params.userId(), params.bundesland(), params.schulnummer());
    }
}

