package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.clients.arix.ArixClient;
import com.fwu.lc_core.licences.clients.lcHalt.LCHaltClient;
import com.fwu.lc_core.licences.models.Licencee;
import com.fwu.lc_core.licences.models.ODRLPolicy;
import com.fwu.lc_core.shared.LicenceHolder;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.ClientLicenseHolderFilterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Slf4j
@Component
public class LicencesCollector {
    private final ClientLicenseHolderFilterService clientLicenseHolderFilterService;
    private final LCHaltClient lcHaltClient;
    private final ArixClient arixClient;

    public LicencesCollector(ClientLicenseHolderFilterService clientLicenseHolderFilterService, @Value("${arix.accepting.url}") String arixUrl, LCHaltClient lcHaltClient) {
        this.clientLicenseHolderFilterService = clientLicenseHolderFilterService;
        this.arixClient = new ArixClient(arixUrl);
        this.lcHaltClient = lcHaltClient;
    }

    public Mono<ODRLPolicy> getODRLPolicy(Licencee licencee, String clientName) {
        var allowedLicenceHolders = clientLicenseHolderFilterService.getAllowedLicenceHolders(clientName);

        if (allowedLicenceHolders.isEmpty()) {
            log.warn("No allowed licence holders found for client: {}", clientName);
            return Mono.empty();
        }

        return collectPermissions(licencee, allowedLicenceHolders)
                .reduce(new ArrayList<ODRLPolicy.Permission>(), (agg, lst) -> {
                    agg.addAll(lst);
                    return agg;
                })
                .map(ODRLPolicy::new)
                .doOnSuccess(policy ->
                        log.info("Found {} licences in total for client: {}", policy.permissions.size(), clientName)
                );
    }

    private Flux<List<ODRLPolicy.Permission>> collectPermissions(Licencee licencee, EnumSet<LicenceHolder> licenceHolders) {
        return Flux.fromIterable(licenceHolders).flatMap(licenceHolder ->
                permissionsFor(licencee, licenceHolder).onErrorResume(e -> {
                    log.error("Error fetching licences from {}: {}", licenceHolder, e.getMessage());
                    return Mono.just(new ArrayList<>());
                }));
    }

    private Mono<List<ODRLPolicy.Permission>> permissionsFor(Licencee licencee, LicenceHolder licenceHolder) {
        return switch (licenceHolder) {
            case LicenceHolder.ARIX -> arixClient.getPermissions(
                    licencee.bundesland(), licencee.standortnummer(), licencee.schulnummer(), licencee.userId());
            case LicenceHolder.LC_HALT -> lcHaltClient.getPermissions(
                    licencee.bundesland(), licencee.standortnummer(), licencee.schulnummer(), licencee.userId());
            default -> Mono.just(new ArrayList<>());
        };
    }
}

