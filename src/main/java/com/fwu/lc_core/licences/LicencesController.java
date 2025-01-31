package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.Licence;
import com.fwu.lc_core.licences.models.LicencesRequestParams;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LicencesController {
    @PostMapping("/v1/licenses/request")
    private String request(LicencesRequestParams params) {
        List<Licence> licenses = LicencesParser.parse(LicencesCollector.getUnparsedLicences(params));
        // TODO: Convert to DTO
        // TODO: Return licenses
        return "";
    }
}
