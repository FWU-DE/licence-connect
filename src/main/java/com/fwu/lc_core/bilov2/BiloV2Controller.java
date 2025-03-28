package com.fwu.lc_core.bilov2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.AvailableLicenceHolders;
import com.fwu.lc_core.shared.clientLicenseHolderFilter.ClientLicenseHolderFilterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@RestController
public class BiloV2Controller {
    @Autowired
    private ClientLicenseHolderFilterService clientLicenseHolderFilterService;

    @Value("${bilo.v2.auth.tokenUrl}")
    private String tokenUrl;

    @Value("${bilo.v2.auth.clientId}")
    private String clientId;

    @Value("${bilo.v2.auth.clientSecret}")
    private String clientSecret;

    @Value("${bilo.v2.auth.licenceUrl}")
    private String licenceUrl;

    @Validated
    @GetMapping("/v1/bilo/request/{userId}")
    public ResponseEntity<String> request(@PathVariable String userId, @RequestParam String clientName) {
        log.info("Received licence request for client: {} and userId: {}", clientName, userId);
        if (!clientLicenseHolderFilterService.getAllowedLicenceHolders(clientName).contains(AvailableLicenceHolders.BILO_V2)) {
            log.warn("Client {} is not allowed to access BILO_V2", clientName);
            return ResponseEntity.ok("[]");
        }
        String bearerToken = fetchAuthToken();
        return fetchLicenses(userId, bearerToken, clientName);
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

    private ResponseEntity<String> fetchLicenses(String licenceeId, String bearerToken, String clientName) {
        String url = licenceUrl + licenceeId + "?inc=license";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("license-user-id", licenceeId);
        headers.set("Authorization", "Bearer " + bearerToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response;
        try {
            response = (new RestTemplate()).exchange(url, HttpMethod.GET, request, String.class);
        } catch (HttpClientErrorException e) {
            // Bilo returns 401 if the user is not found so we cannot do any sophisticated error handling or logging here
            log.warn("No licences found for client: {} and userId: {}", clientName, licenceeId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to fetch licenses");
        }
        log.info("Found licences for client: {} and userId: {}", clientName, licenceeId);
        return ResponseEntity.ok(response.getBody());
    }
}
