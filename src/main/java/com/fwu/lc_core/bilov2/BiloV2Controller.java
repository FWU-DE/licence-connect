package com.fwu.lc_core.bilov2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.AvailableLicenceHolders;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.ClientLicenseHolderFilterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController
public class BiloV2Controller {
    private final ClientLicenseHolderFilterService clientLicenseHolderFilterService;
    @Value("${bilo.v2.auth.tokenUrl}")
    private String tokenUrl;

    @Value("${bilo.v2.auth.clientId}")
    private String clientId;

    @Value("${bilo.v2.auth.clientSecret}")
    private String clientSecret;

    @Value("${bilo.v2.auth.licenceUrl}")
    private String licenceUrl;

    public BiloV2Controller(ClientLicenseHolderFilterService clientLicenseHolderFilterService) {
        this.clientLicenseHolderFilterService = clientLicenseHolderFilterService;
    }


    @Validated
    @PostMapping("/bilo/request/{userId}")
    public ResponseEntity<String> request(@PathVariable String userId, @RequestParam String clientName) {
        if (!clientLicenseHolderFilterService.getAllowedLicenceHolders(clientName).contains(AvailableLicenceHolders.BILO_V2))
            return ResponseEntity.ok("[]");
        String bearerToken = fetchAuthToken();
        return fetchLicenses(userId, bearerToken);
    }

    private String fetchAuthToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = (new RestTemplate()).postForEntity(tokenUrl, request, String.class);
            String responseBody = Optional.ofNullable(response.getBody())
                    .orElseThrow(() -> new RuntimeException("Failed to retrieve access token: response body is null"));
            return Optional.ofNullable(new ObjectMapper().readTree(responseBody).get("access_token"))
                    .map(JsonNode::asText)
                    .orElseThrow(() -> new RuntimeException("Failed to retrieve access token: token is null or empty"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch auth token", e);
        }
    }

    private ResponseEntity<String> fetchLicenses(String licenceeId, String bearerToken) {
        String url = licenceUrl + licenceeId + "?inc=license";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("license-user-id", licenceeId);
            headers.set("Authorization", "Bearer " + bearerToken);
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = (new RestTemplate()).exchange(url, HttpMethod.GET, request, String.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Failed to retrieve licenses: " + response.getStatusCode());
            }
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to fetch licenses");
        }
    }
}
