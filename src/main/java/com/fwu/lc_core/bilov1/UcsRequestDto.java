package com.fwu.lc_core.bilov1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UcsRequestDto {

    @JsonProperty(required = true)
    @NotBlank
    public final String userId;

    @JsonProperty(required = true)
    @NotBlank
    public final String clientId;

    @JsonProperty()
    public final String schulkennung;

    @JsonProperty(required = true)
    @NotNull
    public final Bundesland bundesland;

    public UcsRequestDto(String userId, String clientId, String schulkennung, Bundesland bundesland) {
        this.userId = userId;
        this.clientId = clientId;
        this.schulkennung = schulkennung;
        this.bundesland = bundesland;
    }
}