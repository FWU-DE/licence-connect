package com.fwu.lc_core.licences.collection.arix;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fwu.lc_core.shared.Bundesland;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArixRequestDto {

    @JsonProperty(required = true)
    @NotNull
    public final Bundesland bundesland;

    @JsonProperty()
    public final String schulnummer;

    @JsonProperty()
    public final String standortnummer;

    @JsonProperty()
    public final String userId;

    public ArixRequestDto(Bundesland bundesland, String schulnummer, String standortnummer, String userId) {
        this.bundesland = bundesland;
        this.schulnummer = schulnummer;
        this.standortnummer = standortnummer;
        this.userId = userId;
    }
}