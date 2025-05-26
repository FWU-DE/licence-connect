package com.fwu.lc_core.licences.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fwu.lc_core.shared.Bundesland;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LicencesRequestDto(
        @JsonProperty(required = true) @NotNull Bundesland bundesland,
        @JsonProperty() String standortnummer,
        @JsonProperty() String schulnummer,
        @JsonProperty() String userId) {
}
