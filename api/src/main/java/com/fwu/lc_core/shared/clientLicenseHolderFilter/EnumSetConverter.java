package com.fwu.lc_core.shared.clientLicenseHolderFilter;

import com.fwu.lc_core.licences.models.LicenceHolder;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumSetConverter {

    public static Set<String> toStringSet(EnumSet<LicenceHolder> enumSet) {
        return enumSet.stream().map(Enum::name).collect(Collectors.toSet());
    }

    public static EnumSet<LicenceHolder> toEnumSet(Set<String> stringSet) {
        return stringSet.stream()
                .map(LicenceHolder::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(LicenceHolder.class)));
    }
}
