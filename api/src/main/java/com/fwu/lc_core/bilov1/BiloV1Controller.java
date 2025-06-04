package com.fwu.lc_core.bilov1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fwu.lc_core.shared.Bundesland;
import com.fwu.lc_core.shared.LicenceHolder;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.ClientLicenseHolderFilterService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@Slf4j
@RestController
public class BiloV1Controller {

    @Value("${bilo.v1.base-url}")
    private String baseUrl;

    @Value("${bilo.v1.auth.admin.username}")
    private String technicalUserName;

    @Value("${bilo.v1.auth.admin.password}")
    private String technicalUserPassword;

    @Value("${bilo.v1.auth.endpoint}")
    private String authEndpoint;

    @Value("${bilo.v1.licence.endpoint}")
    private String licenceEndpoint;

    @Autowired
    private ClientLicenseHolderFilterService clientLicenseHolderFilterService;

    @Validated
    @GetMapping("/v1/ucs/request")
    private ResponseEntity<UcsLicenceeDto> request(
            @RequestParam @NotEmpty String userId,
            @RequestParam @NotEmpty String clientId,
            @RequestParam(required = false) String schulkennung,
            @RequestParam @NotNull Bundesland bundesland) {
        log.info("Received licence request for client: {} with Bundesland: {}, Schulkennung: {}, UserId: {}", clientId, bundesland, schulkennung, userId);
        if (!clientLicenseHolderFilterService.getAllowedLicenceHolders(clientId).contains(LicenceHolder.BILO_V1)) {
            log.info("Client {} is not allowed to access BILO_V1", clientId);
            return ResponseEntity.ok(null);
        }
        String bearerToken = fetchAuthToken();
        UcsLicenceeDto licence = fetchLicencees(userId, bearerToken);
        log.info("Found {} licences for client: {}", licence.licenses.size(), clientId);
        return ResponseEntity.ok(licence);
    }

    private String fetchAuthToken() {
        String ucsAuthEndpoint = this.baseUrl + "/" + this.authEndpoint;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("username", technicalUserName);
            body.add("password", technicalUserPassword);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = (new RestTemplate()).postForEntity(ucsAuthEndpoint, request, String.class);
            return new ObjectMapper().readTree(response.getBody()).get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch auth token", e);
        }
    }

    private UcsLicenceeDto fetchLicencees(String licenceeId, String bearerToken) {
        String ucsLicenceEndpoint = baseUrl + "/" + licenceEndpoint + "/" + licenceeId;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<UcsLicenceeDto> response = (new RestTemplate()).exchange(
                    ucsLicenceEndpoint,
                    HttpMethod.GET,
                    entity,
                    UcsLicenceeDto.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch licencees", e);
        }
    }
}
