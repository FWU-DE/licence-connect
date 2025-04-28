package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.LicenceResponse;

import java.util.List;

public class TestHelper {
    public static List<String> extractLicenceCodesFrom(LicenceResponse licence) {
        return licence.permission
                .stream()
                .map(l -> l.target)
                .toList();
    }
}
