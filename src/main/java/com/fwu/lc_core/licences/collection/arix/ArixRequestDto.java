package com.fwu.lc_core.licences.collection.arix;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fwu.lc_core.shared.Bundesland;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArixRequestDto {

    @JsonProperty(required = true)
    public final Bundesland bundesland;

    @JsonProperty()
    public final String standortnummer;

    @JsonProperty()
    public final String schulnummer;

    @JsonProperty()
    public final String userId;

    public ArixRequestDto(Bundesland bundesland, String standortnummer, String schulnummer, String userId) {
        this.bundesland = bundesland;
        this.standortnummer = standortnummer;
        this.schulnummer = schulnummer;
        this.userId = userId;
    }
}
