package com.fwu.lc_core.licences.models;

public enum LicenceHolder {
    ARIX("ARIX"), INVALID_SOURCE("INVALID_SOURCE");

    private final String value;

    LicenceHolder(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
