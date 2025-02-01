package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.Licence;
import com.fwu.lc_core.licences.models.LicencesRequestParams;
import com.fwu.lc_core.licences.models.UnparsedLicences;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RestController
public class LicencesController {
    @PostMapping("/v1/licenses/request")
    private String request(LicencesRequestParams params) {

        Flux<Licence> licenses = LicencesCollector.getUnparsedLicences(params).flatMap(LicencesParser::parse);
        // TODO: Convert to DTO/needed Data format.
        // TODO: Return licenses
        return "";
    }
}
