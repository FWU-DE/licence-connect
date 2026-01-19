package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.Licencee;
import com.fwu.lc_core.shared.Bundesland;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LicenceeFactory {
    private static final String SCHOOL_DATA_CSV = "schooldata/lookup_table.csv";
    private final Map<String, String> schoolDistrictMap = new HashMap<>();
    private final List<String> schulnummerStandortnummerMappingEnabledClients;


    public LicenceeFactory(@Value("${mapping.schulnummer-standortnummer.enabled-clients:}") String schulnummerStandortnummerMappingEnabledClients) {
        this.schulnummerStandortnummerMappingEnabledClients = schulnummerStandortnummerMappingEnabledClients.isBlank()
                ? List.of()
                : List.of(schulnummerStandortnummerMappingEnabledClients.split(","));
        loadSchoolData();
    }

    public Licencee create(String bundesland, String standortnummer, String schulkennung, String userId, String clientName) {
        Bundesland bundeslandTyped = null;
        // Not all licence holders require a bundesland, so we allow it to be null.
        // ARIX requires it, LC-Halt does not.
        if (bundesland != null) {
            bundeslandTyped = Bundesland.fromAbbreviation(bundesland);
        }

        if (bundeslandTyped == Bundesland.BB && standortnummer == null && isSchulnummerStandortnummerMappingEnabledForClient(clientName)) {
            standortnummer = mapSchulnummerToStandortnummer(schulkennung);
        }

        return new Licencee(bundeslandTyped, standortnummer, schulkennung, userId);
    }

    private boolean isSchulnummerStandortnummerMappingEnabledForClient(String clientName) {
        return clientName != null && schulnummerStandortnummerMappingEnabledClients.contains(clientName.trim());
    }

    private String mapSchulnummerToStandortnummer(String schulkennung) {
        if (schulkennung == null) {
            throw new IllegalArgumentException("Schulkennung must be provided for BB bundesland.");
        }
        return schoolDistrictMap.getOrDefault(mapSchulkennungToSchulnummer(schulkennung), null);
    }

    private static String mapSchulkennungToSchulnummer(String schulkennung) {
        // The "schulkennung" from VIDIS is of the form XX-XX-XXXXXX, where the last part is school number.
        var schulnummerParts = schulkennung.split("-");
        if (schulnummerParts.length != 3) {
            throw new IllegalArgumentException("Invalid Schulkennung format for BB: " + schulkennung);
        }
        return schulnummerParts[2];
    }

    private void loadSchoolData() {
        try (BufferedReader br =
                     new BufferedReader(
                             new InputStreamReader(
                                     (new ClassPathResource(SCHOOL_DATA_CSV)).getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                if (values.length >= 4) {
                    String schoolNumber = values[0];
                    String district = values[3];
                    schoolDistrictMap.put(schoolNumber, district);
                }
            }
        } catch (IOException ignored) {
        }
    }
}
