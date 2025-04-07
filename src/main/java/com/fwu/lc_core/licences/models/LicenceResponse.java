package com.fwu.lc_core.licences.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

public class LicenceResponse {
    @JsonProperty("@context")
    public String context = "http://www.w3.org/ns/odrl.jsonld";
    @JsonProperty("@type")
    public String type = "Set";
    public final String uid;
    public List<Permission> permission;

    public LicenceResponse(String target, LicenceHolder assigner, OdrlAction action) {
        this.permission = Collections.singletonList(new Permission(target, assigner, action));
        this.uid = assigner + "." + target;
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
