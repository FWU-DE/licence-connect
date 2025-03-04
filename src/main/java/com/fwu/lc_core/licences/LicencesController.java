package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.collection.LicencesCollector;
import com.fwu.lc_core.licences.models.Licence;
import com.fwu.lc_core.licences.models.LicencesRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
public class LicencesController {
    @PostMapping("/v1/licences/request")
    private ResponseEntity<List<Licence>> request(@Valid @RequestBody LicencesRequestDto requestDto, @RequestParam String clientName) {
        if (!paramsValid(requestDto)) {
            return ResponseEntity.badRequest().build();
        }

        Mono<List<Licence>> licences = LicencesCollector.getUnparsedLicences(requestDto, clientName).flatMap(LicencesParser::parse).collectList();
        return licences
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.internalServerError().build()))
                .block();
    }

    private boolean paramsValid(LicencesRequestDto params) {
        return params.bundesland() != null && (params.standortnummer() != null || params.schulnummer() == null) && (params.schulnummer() != null || params.userId() == null);
    }
}
