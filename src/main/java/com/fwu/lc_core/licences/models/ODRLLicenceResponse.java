package com.fwu.lc_core.licences.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
public class ODRLLicenceResponse {
    @JsonProperty("@context")
    public String context = "http://www.w3.org/ns/odrl.jsonld";
    @JsonProperty("@type")
    public String type = "Set";
    public String uid;
    public List<Permission> permission;


    public ODRLLicenceResponse(List<String> target, LicenceHolder assigner, OdrlAction action) {
        this.permission = target.stream()
                .map(t -> new Permission(t, assigner, action))
                .toList();
        this.uid = "urn:uuid:" + UUID.randomUUID().toString();
    }

    @NoArgsConstructor
    public static class Permission {
        public String target;
        public LicenceHolder assigner;
        public OdrlAction action;
        
        public Permission(String target, LicenceHolder assigner, OdrlAction action) {
            this.target = target;
            this.assigner = assigner;
            this.action = action;
        }
    }
}
