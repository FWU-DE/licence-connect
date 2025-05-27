package com.fwu.lc_core.licences.models;

import lombok.Getter;

@Getter
public enum LicenceHolder {
    ARIX("ARIX"),
    LC_HALT("LC_HALT"),
    INVALID_SOURCE("INVALID_SOURCE");

    private final String value;

    LicenceHolder(String value) {
        this.value = value;
    }
}
