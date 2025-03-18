package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.collection.LicencesCollector;
import com.fwu.lc_core.licences.models.Licence;
import com.fwu.lc_core.licences.models.LicencesRequestDto;
import jakarta.validation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Slf4j
@RestController
public class LicencesController {
    @GetMapping("/v1/licences/request")
    private Mono<ResponseEntity<List<Licence>>> request(
            @Valid @ValidLicencesRequest LicencesRequestDto requestDto,
            @RequestParam String clientName) {
        log.info("Received licence request for client: {}", clientName);

        Mono<List<Licence>> licences = LicencesCollector.getUnparsedLicences(requestDto, clientName).flatMap(LicencesParser::parse).collectList();
        return licences
                .map(licenceList -> {
                    log.info("Found {} licences for client: {}", licenceList.size(), clientName);
                    return ResponseEntity.ok(licenceList);
                })
                .onErrorResume(e -> {
                            log.error("Error collecting licences: {}", e.getMessage());
                            return Mono.empty();
                        }
                );
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
