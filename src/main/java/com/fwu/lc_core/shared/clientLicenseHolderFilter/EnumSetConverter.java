package com.fwu.lc_core.shared.clientLicenseHolderFilter;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumSetConverter {

    public static Set<String> toStringSet(EnumSet<AvailableLicenceHolders> enumSet) {
        return enumSet.stream().map(Enum::name).collect(Collectors.toSet());
    }

    public static EnumSet<AvailableLicenceHolders> toEnumSet(Set<String> stringSet) {
        return stringSet.stream()
                .map(AvailableLicenceHolders::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(AvailableLicenceHolders.class)));
    }
}
