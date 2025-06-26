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
                                    @ExampleObject(name= "STK", value = "STK"),
                                    @ExampleObject(name= "BB", value = "BB"),
                                    @ExampleObject(name ="DE-BB", value = "DE-BB"),
                                    @ExampleObject(name= "MV", value = "MV"),
                                    @ExampleObject(name= "DE-MV", value = "DE-MV"),
                                    @ExampleObject(name= "RP", value = "RP"),
                                    @ExampleObject(name= "DE-RP", value = "DE-RP"),
                                    @ExampleObject(name= "BW", value = "BW"),
                                    @ExampleObject(name= "DE-BW", value = "DE-BW"),
                                    @ExampleObject(name= "BY", value = "BY"),
                                    @ExampleObject(name= "DE-BY", value = "DE-BY"),
                                    @ExampleObject(name= "BE", value = "BE"),
                                    @ExampleObject(name= "DE-BE", value = "DE-BE"),
                                    @ExampleObject(name= "HB", value = "HB"),
                                    @ExampleObject(name= "DE-HB", value = "DE-HB"),
                                    @ExampleObject(name= "HH", value = "HH"),
                                    @ExampleObject(name= "DE-HH", value = "DE-HH"),
                                    @ExampleObject(name= "HE", value = "HE"),
                                    @ExampleObject(name= "DE-HE", value = "DE-HE"),
                                    @ExampleObject(name= "NI", value = "NI"),
                                    @ExampleObject(name= "DE-NI", value = "DE-NI"),
                                    @ExampleObject(name= "NW", value = "NW"),
                                    @ExampleObject(name= "DE-NW", value = "DE-NW"),
                                    @ExampleObject(name= "SL", value = "SL"),
                                    @ExampleObject(name= "DE-SL", value = "DE-SL"),
                                    @ExampleObject(name= "SN", value = "SN"),
                                    @ExampleObject(name= "DE-SN", value = "DE-SN"),
                                    @ExampleObject(name= "ST", value = "ST"),
                                    @ExampleObject(name= "DE-ST", value = "DE-ST"),
                                    @ExampleObject(name= "SH", value = "SH"),
                                    @ExampleObject(name= "DE-SH", value = "DE-SH"),
                                    @ExampleObject(name= "TH", value = "TH"),
                                    @ExampleObject(name= "DE-TH", value = "DE-TH"),
                                })})
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

        Bundesland bundeslandTyped = null;
        if (bundesland != null) {
            try {
                bundeslandTyped = Bundesland.fromAbbreviation(bundesland);
            } catch (IllegalArgumentException e) {
                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
            }
        }

        var licencee = new Licencee(bundeslandTyped, standortnummer, schulnummer, userId);
        return licencesCollector.getODRLPolicy(licencee, clientName);
    }
}

