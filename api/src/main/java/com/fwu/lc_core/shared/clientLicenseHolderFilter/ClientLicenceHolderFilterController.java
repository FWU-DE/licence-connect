package com.fwu.lc_core.shared.clientLicenseHolderFilter;

import com.fwu.lc_core.config.swagger.SwaggerConfig;
import com.fwu.lc_core.shared.LicenceHolder;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;


@Slf4j
@Tag(
        name = "Admin",
        description = "Manage access to licence holders for clients",
        extensions = @Extension(properties = {@ExtensionProperty(name = SwaggerConfig.TAG_ORDER, value = "2")})
)
@RestController
@Validated
@AllArgsConstructor
public class ClientLicenceHolderFilterController {
    private final ClientLicenseHolderFilterService clientLicenseHolderFilterService;

    @GetMapping("/admin/client-licence-holder-mapping/{clientName}")
    public ResponseEntity<EnumSet<LicenceHolder>> getLicenceHolders(@PathVariable String clientName) {
        log.info("Received request to get licence holders for client: {}", clientName);
        return ResponseEntity.ok(clientLicenseHolderFilterService.getAllowedLicenceHolders(clientName));
    }

    @PutMapping("/admin/client-licence-holder-mapping/{clientName}")
    public ResponseEntity<Object> updateLicenceHolder(
            @RequestBody ClientLicenceHolderMappingDto newMapping,
            @PathVariable String clientName
    ) {
        log.info("Setting new available licence holders for client: {}", clientName);
        clientLicenseHolderFilterService.setAllowedLicenceHolders(clientName, newMapping.availableLicenceHolders());
        log.info("New allowed systems: {} for client: {}", newMapping.availableLicenceHolders().toString(), clientName);
        return ResponseEntity.noContent().build();
    }
}
