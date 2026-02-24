package com.fwu.lc_core.licences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fwu.lc_core.licences.models.Licencee;
import com.fwu.lc_core.shared.Bundesland;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
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

    public Licencee create(String bundesland, String standortnummer, String schulnummer, String clientName) {
        Bundesland bundeslandTyped = Bundesland.fromAbbreviation(bundesland);

        if (bundeslandTyped == Bundesland.BB && standortnummer == null && schulnummer != null && isSchulnummerStandortnummerMappingEnabledForClient(clientName)) {
            standortnummer = mapSchulnummerToStandortnummer(schulnummer);
        }

        return new Licencee(bundeslandTyped, standortnummer, schulnummer);
    }

    private boolean isSchulnummerStandortnummerMappingEnabledForClient(String clientName) {
        return clientName != null && schulnummerStandortnummerMappingEnabledClients.contains(clientName.trim());
    }

    private String mapSchulnummerToStandortnummer(String schulnummer) {
        var extractedSchulnummer = schulnummer;
        if (isValidSchulkennungFormat(schulnummer)) {
            extractedSchulnummer = mapSchulkennungToSchulnummer(schulnummer);
        }

        var mappedSchoolDistrict = schoolDistrictMap.get(extractedSchulnummer);
        if (mappedSchoolDistrict == null) {
            throw new IllegalArgumentException("Invalid Schulkennung or schulnummer format: " + schulnummer);
        }

        return mappedSchoolDistrict;
    }

    private static Boolean isValidSchulkennungFormat(String schulnummerOrSchulkennung) {
        return schulnummerOrSchulkennung.matches("^[A-Z]{2}-[A-Z]{2}-\\d+$");
    }

    private static String mapSchulkennungToSchulnummer(String schulkennung) {
        // The "schulkennung" from VIDIS is of the form DE-BB-XXXXXX, where the last part is school number.
        var schulnummerParts = schulkennung.split("-");
        
        if (schulnummerParts.length != 3) {
            log.error("Invalid Schulkennung format provided: {}", schulkennung);
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
