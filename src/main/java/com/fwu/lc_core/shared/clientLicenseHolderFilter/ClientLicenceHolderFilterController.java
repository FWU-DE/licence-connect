package com.fwu.lc_core.shared.clientLicenseHolderFilter;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;

@RestController
@Validated
@AllArgsConstructor
public class ClientLicenceHolderFilterController {

    private final ClientLicenseHolderFilterService clientLicenseHolderFilterService;

    @GetMapping("/admin/client-licence-holder-mapping/{clientName}")
    public ResponseEntity<EnumSet<AvailableLicenceHolders>> getLicenceHolders(@PathVariable String clientName) {
        return ResponseEntity.ok(clientLicenseHolderFilterService.getAllowedLicenceHolders(clientName));
    }

    @PutMapping("/admin/client-licence-holder-mapping/{clientName}")
    public ResponseEntity<String> updateLicenceHolder(
            @RequestBody ClientLicenceHolderMappingDto newMapping,
            @PathVariable String clientName
    ) {
        clientLicenseHolderFilterService.setAllowedLicenceHolders(clientName, newMapping.availableLicenceHolders);
        return ResponseEntity.noContent().build();
    }
}
