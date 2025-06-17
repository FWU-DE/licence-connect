package com.fwu.lc_core.licences.models;

/**
 * Represents a licencee with associated details such as Bundesland, Standortnummer, Schulnummer, and UserId.
 */
public record Licencee(
        String bundesland,
        String standortnummer,
        String schulnummer,
        String userId) {
}
