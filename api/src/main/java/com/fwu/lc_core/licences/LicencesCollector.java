package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.clients.ArixClient;
import com.fwu.lc_core.licences.clients.LCHaltClient;
import com.fwu.lc_core.licences.models.Licencee;
import com.fwu.lc_core.licences.models.ODRLPolicy;
import com.fwu.lc_core.licences.models.LicenceHolder;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.ClientLicenseHolderFilterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
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

    public Mono<ODRLPolicy> getODRLPolicy(Licencee params, String clientName) {
        return Mono.zip(collectPermissions(params, clientName), results -> {
            // FIXME: This is a workaround for the type erasure that occurs with Mono.zip
            // I'd rather have a proper type check here or another way of
            // concatenating a 0-n list of lists of permissions with Mono.zip.
            @SuppressWarnings("unchecked")
            var permissions = Arrays.stream(results)
                    .map(r -> (List<ODRLPolicy.Permission>) r)
                    .flatMap(List::stream)
                    .toList();

            log.info("Found {} licences in total for client: {}", permissions.size(), clientName);
            return new ODRLPolicy(permissions);
        });
    }

    private List<Mono<List<ODRLPolicy.Permission>>> collectPermissions(Licencee params, String clientName) {
        var allowedLicenceHolders = clientLicenseHolderFilterService.getAllowedLicenceHolders(clientName);
        var publishers = new ArrayList<Mono<List<ODRLPolicy.Permission>>>();

        if (allowedLicenceHolders.contains(LicenceHolder.ARIX)) {
            var arixPermissions = arixClient.getPermissions(params.bundesland(), params.standortnummer(), params.schulnummer(), params.userId());
            publishers.add(arixPermissions.onErrorResume(
                    e -> {
                        log.error("Error fetching ARIX licences: {}", e.getMessage());
                        return Mono.just(new ArrayList<>());
                    }
            ));
        }

        if (allowedLicenceHolders.contains(LicenceHolder.LC_HALT)) {
            var lcHaltPermissions = lcHaltClient.getPermissions(params.bundesland(), params.standortnummer(), params.schulnummer(), params.userId());
            publishers.add(lcHaltPermissions.onErrorResume(
                    e -> {
                        log.error("Error fetching LC-Halt licences: {}", e.getMessage());
                        return Mono.just(new ArrayList<>());
                    }
            ));
        }

        return publishers;
    }
}

