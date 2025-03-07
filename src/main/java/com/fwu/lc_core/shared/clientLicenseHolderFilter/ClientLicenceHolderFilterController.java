package com.fwu.lc_core.shared.clientLicenseHolderFilter;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;

@RestController
@Validated
@AllArgsConstructor
public class ClientLicenceHolderFilterController {

    private final Logger log = LoggerFactory.getLogger(ClientLicenceHolderFilterController.class);
    private final ClientLicenseHolderFilterService clientLicenseHolderFilterService;

    @GetMapping("/admin/client-licence-holder-mapping/{clientName}")
    public ResponseEntity<EnumSet<AvailableLicenceHolders>> getLicenceHolders(@PathVariable String clientName) {
        log.info("Received request to get licence holders for client: " + clientName);
        return ResponseEntity.ok(clientLicenseHolderFilterService.getAllowedLicenceHolders(clientName));
    }

    @PutMapping("/admin/client-licence-holder-mapping/{clientName}")
    public ResponseEntity<String> updateLicenceHolder(
            @RequestBody ClientLicenceHolderMappingDto newMapping,
            @PathVariable String clientName
    ) {
        clientLicenseHolderFilterService.setAllowedLicenceHolders(clientName, newMapping.availableLicenceHolders);
        log.info("Received request to set licence holders for client: " + clientName+ ". New allowed systems: " + newMapping.availableLicenceHolders.toString());
        return ResponseEntity.noContent().build();
    }
}
