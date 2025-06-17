package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.Licencee;
import com.fwu.lc_core.licences.models.ODRLPolicy;

import com.fwu.lc_core.shared.Bundesland;
import com.fwu.lc_core.shared.validators.EnumValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class LicencesController {

    final LicencesCollector licencesCollector;

    public LicencesController(LicencesCollector licencesCollector) {
        this.licencesCollector = licencesCollector;
    }

    @GetMapping("/v1/licences/request")
    private Mono<ODRLPolicy> request(
        @EnumValue(enumClass = Bundesland.class) @RequestParam(required = false) String bundesland,
        @RequestParam(required = false) String standortnummer,
        @RequestParam(required = false) String schulnummer,
        @RequestParam(required = false) String userId, 
        @RequestParam String clientName) {

        

        log.info(
                "GET /v1/licences/request: clientName: {}; bundesland: {}; standortnummer: {}; schulnummer: {}; userId: {};",
                clientName, bundesland, standortnummer, schulnummer, userId
        );

        var licencee = new Licencee(bundesland, standortnummer, schulnummer, userId);
        return licencesCollector.getODRLPolicy(licencee, clientName);
    }
}

