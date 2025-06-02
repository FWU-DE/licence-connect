package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.ODRLPolicy;

import java.util.List;

public class TestHelper {
    public static List<String> extractLicenceCodesFrom(ODRLPolicy licence) {
        return licence.permissions
                .stream()
                .map(l -> l.target)
                .toList();
    }
}
