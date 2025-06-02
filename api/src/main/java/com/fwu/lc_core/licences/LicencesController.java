package com.fwu.lc_core.licences;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fwu.lc_core.licences.models.Licencee;
import com.fwu.lc_core.licences.models.ODRLPolicy;
import com.fwu.lc_core.shared.Bundesland;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
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
    private Mono<ODRLPolicy> request(@ParameterObject LicencesRequestDto requestDto, @RequestParam String clientName) {
        log.info("Received licence request for client: {}", clientName);

        var licencee = new Licencee(
                requestDto.bundesland(),
                requestDto.standortnummer(),
                requestDto.schulnummer(),
                requestDto.userId()
        );

        return licencesCollector.getODRLPolicy(licencee, clientName);
    }
}


@JsonInclude(JsonInclude.Include.NON_NULL)
record LicencesRequestDto(
        @JsonProperty Bundesland bundesland,
        @JsonProperty String standortnummer,
        @JsonProperty String schulnummer,
        @JsonProperty String userId) {
}
