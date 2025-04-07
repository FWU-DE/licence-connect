package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.LicenceResponse;

import java.util.List;
import java.util.stream.Collectors;

public class TestHelper {
    public static List<String> extractLicenceCodesFrom(List<LicenceResponse> licences) {
        return licences.stream()
                .map(l -> l.permission.stream()
                        .map(permission -> permission.target)
                        .toList()
                )
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
