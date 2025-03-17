package com.fwu.lc_core.shared.clientLicenseHolderFilter;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.EnumSet;


@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class ClientLicenceHolderFilterController {
    private final ClientLicenseHolderFilterService clientLicenseHolderFilterService;

    @GetMapping("/admin/client-licence-holder-mapping/{clientName}")
    public Mono<ResponseEntity<EnumSet<AvailableLicenceHolders>>> getLicenceHolders(@PathVariable String clientName) {
        log.info("Received request to get licence holders for client: {}", clientName);
        return Mono.just(ResponseEntity.ok(clientLicenseHolderFilterService.getAllowedLicenceHolders(clientName)));
    }

    @PutMapping("/admin/client-licence-holder-mapping/{clientName}")
    public Mono<ResponseEntity<Object>> updateLicenceHolder(
            @RequestBody ClientLicenceHolderMappingDto newMapping,
            @PathVariable String clientName
    ) {
        log.info("Setting new available licence holders for client: {}", clientName);
        clientLicenseHolderFilterService.setAllowedLicenceHolders(clientName, newMapping.availableLicenceHolders);
        log.info("New allowed systems: {} for client: {}", newMapping.availableLicenceHolders.toString(), clientName);
        return Mono.just(ResponseEntity.noContent().build());
    }
}
