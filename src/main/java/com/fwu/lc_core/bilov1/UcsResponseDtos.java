package com.fwu.lc_core.bilov1;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Context {
    public static class UcsClassDto {
        public String name;
        public String id;
        public List<String> licenses;
    }

    public static class UcsWorkgroupDto {
        public String name;
        public String id;
        public List<String> licenses;
    }

    public List<String> licenses;
    public List<UcsClassDto> classes;
    public List<UcsWorkgroupDto> workgroups;
    public String school_authority;
    public String school_identifier;
    public String school_name;
    public List<String> roles;
}

class UcsLicenceeDto {
    public String id;
    public String first_name;
    public String last_name;
    public ArrayList<String> licenses; // we cannot be consistent with the naming since this is coming from BILO
    public Map<String, Context> context;
}