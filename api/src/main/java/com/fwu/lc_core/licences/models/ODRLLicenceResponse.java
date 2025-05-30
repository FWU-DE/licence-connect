package com.fwu.lc_core.licences.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.UUID;


public class ODRLLicenceResponse {
    @JsonProperty("@context")
    public final String context = "http://www.w3.org/ns/odrl.jsonld";
    @JsonProperty("@type")
    public final String type = "Set";

    public String uid;
    public List<Permission> permissions;

    public ODRLLicenceResponse(List<Permission> permissions) {
        this.permissions = permissions;
        this.uid = "urn:uuid:" + UUID.randomUUID();
    }

    @AllArgsConstructor
    public static class Permission {
        public String target;
        public LicenceHolder assigner;
        public ODRLAction action;
    }
}
