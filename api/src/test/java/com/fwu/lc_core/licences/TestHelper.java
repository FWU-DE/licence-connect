package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.ODRLLicenceResponse;

import java.util.List;

public class TestHelper {
    public static List<String> extractLicenceCodesFrom(ODRLLicenceResponse licence) {
        return licence.permissions
                .stream()
                .map(l -> l.target)
                .toList();
    }

    public static List<String> extractLicenceCodesFrom(List<ODRLLicenceResponse> licences) {
        return licences.stream()
                .flatMap(licence -> licence.permissions.stream())
                .map(l -> l.target)
                .toList();
    }
}
