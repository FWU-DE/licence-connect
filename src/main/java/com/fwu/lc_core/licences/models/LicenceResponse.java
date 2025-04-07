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

    public LicenceResponse(String target, String assignee, String assigner, String action) {
        this.permission = Collections.singletonList(new Permission(target, assignee, assigner, action));
    }

    @NoArgsConstructor
    public static class Permission {
        public String target;
        public String assignee;
        public String assigner;
        public String action;
        
        public Permission(String target, String assignee, String assigner, String action) {
            this.target = target;
            this.assignee = assignee;
            this.assigner = assigner;
            this.action = action;
        }
    }
}
