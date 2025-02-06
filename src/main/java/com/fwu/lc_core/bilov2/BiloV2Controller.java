package com.fwu.lc_core.bilov2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
public class BiloV2Controller {
    @Value("${bilo.v2.auth.tokenUrl}")
    private String tokenUrl = "";

    @Value("${bilo.v2.auth.clientId}")
    private String clientId;

    @Value("${bilo.v2.auth.clientSecret}")
    private String clientSecret = "";

    @Value("${bilo.v2.auth.licenceUrl}")
    private String licenceUrl = "";

    @Validated
    @PostMapping("/bilo/request/{userId}")
    private ResponseEntity<String> request(@PathVariable String userId) {
        String bearerToken = fetchAuthToken();
        return ResponseEntity.ok(fetchLicenses(userId, bearerToken));
    }

    private String fetchAuthToken() {
        String ucsAuthEndpoint = tokenUrl;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = new RestTemplate().postForEntity(ucsAuthEndpoint, request, String.class);
            String responseBody = response.getBody();
            if (responseBody == null) {
                throw new RuntimeException("Failed to retrieve access token: response body is null");
            }
            String token = new ObjectMapper().readTree(responseBody).get("access_token").asText();
            if (token == null || token.isEmpty()) {
                throw new RuntimeException("Failed to retrieve access token: token is null or empty");
            }
            return token;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch auth token", e);
        }
    }

    private String fetchLicenses(String licenceeId, String bearerToken) {
        String url = licenceUrl + licenceeId + "?inc=license";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("license-user-id", licenceeId);
            headers.set("Authorization", "Bearer " + bearerToken);
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = new RestTemplate().exchange(url, HttpMethod.GET, request, String.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Failed to retrieve licenses: " + response.getStatusCode());
            }
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch licenses", e);
        }
    }
}
