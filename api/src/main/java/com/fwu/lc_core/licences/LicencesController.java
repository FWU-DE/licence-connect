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
    private Mono<ODRLLicenceResponse> request(
            @Valid @ValidLicencesRequest @ParameterObject LicencesRequestDto requestDto,
            @RequestParam String clientName) {
        log.info("Received licence request for client: {}", clientName);
        return licencesCollector.getODRLLicenceResponse(requestDto, clientName);
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
