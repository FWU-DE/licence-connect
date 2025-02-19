package com.fwu.lc_core.licences.collection.arix;

import com.fwu.lc_core.licences.models.UnparsedLicences;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArixController {

    @Value("${mocks.arix.accepting.url}")
    private String baseUrlAccepting;

    @Validated
    @GetMapping("/arix/request")
    private ResponseEntity<String> request(@Valid @RequestBody ArixRequestDto request) {
        ArixClient arixClientAccepting = new ArixClient(baseUrlAccepting);

        UnparsedLicences unparsedLicences = arixClientAccepting.getLicences(
                request.bundesland,
                request.standortnummer,
                request.schulnummer,
                request.userId).block();
        return ResponseEntity.ok(unparsedLicences.rawResult);
    }

}
