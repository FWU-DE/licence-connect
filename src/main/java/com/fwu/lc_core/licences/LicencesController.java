package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.collection.LicencesCollector;
import com.fwu.lc_core.licences.models.Licence;
import com.fwu.lc_core.licences.models.LicencesRequestDto;
import com.fwu.lc_core.shared.Bundesland;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import com.fwu.lc_core.licences.models.LicencesRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@RestController
public class LicencesController {
    private final Logger log = LoggerFactory.getLogger(LicencesController.class);

    @GetMapping("/v1/licences/request")
    private ResponseEntity<List<Licence>> request(
            @Valid @ValidLicencesRequest LicencesRequestDto requestDto,
            @RequestParam String clientName) {
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        log.info("Received licence request for client: {}", clientName);

        Mono<List<Licence>> licences = LicencesCollector.getUnparsedLicences(requestDto, clientName).flatMap(LicencesParser::parse).collectList();
        return licences
                .map(licenceList -> {
                    if (contextMap != null)
                        MDC.setContextMap(contextMap);
                    log.info("Found {} licences for client: {}", licenceList.size(), clientName);
                    return ResponseEntity.ok(licenceList);
                })
                .onErrorResume(e -> {
                            if (contextMap != null)
                                MDC.setContextMap(contextMap);
                            log.error("Error collecting licences: {}", e.getMessage());
                            return Mono.empty();
                        }
                )
                .block();
    }
}


class LicencesRequestValidator implements ConstraintValidator<ValidLicencesRequest, LicencesRequestDto> {
    @Override
    public boolean isValid(LicencesRequestDto params, ConstraintValidatorContext context) {
        return params.bundesland() != null &&
                (params.standortnummer() != null || params.schulnummer() == null) &&
                (params.schulnummer() != null || params.userId() == null);
    }
}

@Constraint(validatedBy = LicencesRequestValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@interface ValidLicencesRequest {
    String message() default "Invalid request parameters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
