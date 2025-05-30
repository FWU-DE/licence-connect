package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.LicencesRequestDto;
import com.fwu.lc_core.licences.models.ODRLLicenceResponse;
import jakarta.validation.*;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Slf4j
@RestController
public class LicencesController {

    @Autowired
    LicencesCollector licencesCollector;

    @GetMapping("/v1/licences/request")
    private Mono<ODRLLicenceResponse> request(@ParameterObject LicencesRequestDto requestDto, @RequestParam String clientName) {
        log.info("Received licence request for client: {}", clientName);
        return licencesCollector.getODRLLicenceResponse(requestDto, clientName);
    }
}
