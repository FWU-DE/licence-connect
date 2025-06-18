package com.fwu.lc_core.licences.models;

import com.fwu.lc_core.shared.Bundesland;

/**
 * Represents a licencee with associated details such as Bundesland, Standortnummer, Schulnummer, and UserId.
 */
public record Licencee(
        Bundesland bundesland,
        String standortnummer,
        String schulnummer,
        String userId) {
}
