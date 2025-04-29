package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.ODRLlicenceResponse;

import java.util.List;

public class TestHelper {
    public static List<String> extractLicenceCodesFrom(ODRLlicenceResponse licence) {
        return licence.permission
                .stream()
                .map(l -> l.target)
                .toList();
    }
}
