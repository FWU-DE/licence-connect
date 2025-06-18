package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.clients.arix.ArixClient;
import com.fwu.lc_core.licences.clients.lcHalt.LCHaltClient;
import com.fwu.lc_core.licences.models.Licencee;
import com.fwu.lc_core.licences.models.ODRLPolicy;
import com.fwu.lc_core.shared.LicenceHolder;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.ClientLicenseHolderFilterService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ClientLicenseHolderFilterService clientLicenseHolderFilterService;

    @Autowired(required = false)
    private LCHaltClient lcHaltClient;

    @Autowired
    private ArixClient arixClient;

    @Value("${lc-halt.enabled}")
    private String lcHaltEnabled;

    public Mono<ODRLPolicy> getODRLPolicy(Licencee licencee, String clientName) {
        var allowedLicenceHolders = clientLicenseHolderFilterService.getAllowedLicenceHolders(clientName);

        if (allowedLicenceHolders.isEmpty()) {
            log.warn("No allowed licence holders found for client: {}", clientName);
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
        if (licenceHolder.equals(LicenceHolder.ARIX)) {
            return arixClient.getPermissions(licencee.bundesland(), licencee.standortnummer(), licencee.schulnummer(), licencee.userId());
        }
        if (licenceHolder.equals(LicenceHolder.LC_HALT) && "true".equals(lcHaltEnabled)) {
            return lcHaltClient.getPermissions(licencee.bundesland(), licencee.standortnummer(), licencee.schulnummer(), licencee.userId());
        }
        return Mono.just(new ArrayList<>());
    }
}

