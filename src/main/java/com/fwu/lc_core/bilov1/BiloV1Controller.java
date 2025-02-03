package com.fwu.lc_core.bilov1;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.client.RestTemplate;


@RestController
public class BiloV1Controller {
    @Value("${ucs.base-url}")
    private String baseUrl = "";

    @Value("${ucs.auth.admin.username}")
    private String technicalUserName;

    @Value("${ucs.auth.admin.password}")
    private String technicalUserPassword = "";

    @Value("${ucs.auth.endpoint}")
    private String authEndpoint = "";

    @Value("${ucs.licence.endpoint}")
    private String licenceEndpoint = "";

    @Validated
    @PostMapping("/v1/ucs/request")
    private String request(@Valid @RequestBody UcsRequestDto request) {
        String bearerToken = fetchAuthToken();
        UcsLicenceeDto ucsLicenceeDto = fetchStudents(request.userId, bearerToken);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return objectMapper.writeValueAsString(ucsLicenceeDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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

    private UcsLicenceeDto fetchStudents(String studentId, String bearerToken) {
        String ucsLicenceEndpoint = baseUrl + "/" + licenceEndpoint + "/" + studentId;
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
            throw new RuntimeException("Failed to fetch students", e);
        }
    }
}
