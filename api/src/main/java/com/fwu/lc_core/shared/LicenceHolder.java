package com.fwu.lc_core.shared;

import lombok.Getter;

@Getter
public enum LicenceHolder {
    ARIX("ARIX"),
    LC_HALT("LC_HALT"),
    BILO_V2("BILO_V2");

    private final String value;

    LicenceHolder(String value) {
        this.value = value;
    }
}
