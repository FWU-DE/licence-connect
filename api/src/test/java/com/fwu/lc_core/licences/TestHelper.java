package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.ODRLLicenceResponse;

import java.util.List;

public class TestHelper {
    public static List<String> extractLicenceCodesFrom(ODRLLicenceResponse licence) {
        return licence.permission
                .stream()
                .map(l -> l.target)
                .toList();
    }
}
