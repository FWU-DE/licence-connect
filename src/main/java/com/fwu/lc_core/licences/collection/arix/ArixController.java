package com.fwu.lc_core.licences.collection.arix;

import com.fwu.lc_core.licences.models.UnparsedLicences;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArixController {

    @Autowired
    ArixClientAccepting arixClientAccepting;

    @Validated
    @PostMapping("/arix/request")
    private ResponseEntity<String> request(@Valid @RequestBody ArixRequestDto request) {
        try {
            UnparsedLicences unparsedLicences = arixClientAccepting.getLicences(
                    request.bundesland,
                    request.standortnummer,
                    request.schulnummer,
                    request.userId).block();

            return ResponseEntity.ok(unparsedLicences.rawResult);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
