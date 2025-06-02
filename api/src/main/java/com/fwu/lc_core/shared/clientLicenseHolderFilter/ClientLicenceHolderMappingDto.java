package com.fwu.lc_core.shared.clientLicenseHolderFilter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fwu.lc_core.shared.LicenceHolder;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

import java.util.EnumSet;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class ClientLicenceHolderMappingDto {
    @JsonProperty(required = true)
    @NotNull
    public final EnumSet<LicenceHolder> availableLicenceHolders;
}
