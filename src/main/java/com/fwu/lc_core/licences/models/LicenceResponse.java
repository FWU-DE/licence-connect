package com.fwu.lc_core.licences.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class LicenceResponse {
    @JsonProperty("@context")
    public String context = "http://www.w3.org/ns/odrl.jsonld";
    
    @JsonProperty("@type")
    public String type = "Set";
    
    public String uid = "https://w3c.github.io/odrl/bp/examples/2";
    public List<Permission> permission;

    public LicenceResponse(String target, LicenceHolder assigner, OdrlAction action) {
        this.permission = Collections.singletonList(new Permission(target, assigner, action));
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
