package com.fwu.lc_core.licences.models;

import com.fwu.lc_core.shared.Bundesland;

/**
 * Represents a licencee with associated details such as Bundesland, Standortnummer, and Schulnummer.
 */
public record Licencee(
        Bundesland bundesland,
        String standortnummer,
        String schulnummer) {
}
