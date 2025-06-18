package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.Licencee;
import com.fwu.lc_core.licences.models.ODRLPolicy;

import com.fwu.lc_core.shared.Bundesland;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class LicencesController {

    final LicencesCollector licencesCollector;

    public LicencesController(LicencesCollector licencesCollector) {
        this.licencesCollector = licencesCollector;
    }

    @Operation(summary = "Fetch licences for the given user.",
            parameters = {
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "bundesland",
                            examples = {
                                    @ExampleObject(name= "BB", value = "BB"),
                                    @ExampleObject(name ="DE-BB", value = "DE-BB")})})
    @GetMapping("/v1/licences/request")
    private Mono<ODRLPolicy> request(
            @RequestParam(required = false) String bundesland,
            @RequestParam(required = false) String standortnummer,
            @RequestParam(required = false) String schulnummer,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String clientName) {

        log.info(
                "GET /v1/licences/request: clientName: {}; bundesland: {}; standortnummer: {}; schulnummer: {}; userId: {};",
                clientName, bundesland, standortnummer, schulnummer, userId
        );
        if (clientName == null) {
            log.error("ClientName not defined, returning 400.");
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
        }

        Bundesland bundeslandTyped;
        try {
            bundeslandTyped = Bundesland.fromAbbreviation(bundesland);
        } catch (IllegalArgumentException e) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
        }

        var licencee = new Licencee(bundeslandTyped, standortnummer, schulnummer, userId);
        return licencesCollector.getODRLPolicy(licencee, clientName);
    }
}

