package com.fwu.lc_core.bilov1;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;

import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
public class BiloV1Controller {


    @Value("${ucs.base-url}")
    private final String baseUrl = "";

    @Value("${ucs.auth.admin.username}")
    private String technicalUserName;

    @Value("${ucs.auth.admin.password}")
    private final String technicalUserPassword = "";

    @Value("${ucs.auth.endpoint}")
    private final String authEndpoint = "";

    @Value("${ucs.licence.endpoint}")
    private final String licenceEndpoint = "";


    private static class Context {
        public static class UcsClassDto {
            public String name;
            public String id;
            public List<String> licenses;
        }

        public static class UcsWorkgroupDto {
            public String name;
            public String id;
            public List<String> licenses;
        }

        public List<String> licenses;
        public List<UcsClassDto> classes;
        public List<UcsWorkgroupDto> workgroups;
        public String school_authority;
        public String school_identifier;
        public String school_name;
        public List<String> roles;
    }

    private static class UcsStudent {
        public String id;
        public String first_name;
        public String last_name;
        public ArrayList<String> licenses;
        public Map<String, Context> context;
    }


    @Validated
    @PostMapping("/v1/ucs/request")
    private String request(@Valid @RequestBody UcsRequestDto request) {
        String bearerToken = fetchAuthToken();

        UcsStudent ucsStudent = fetchStudents(request.userId, bearerToken);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return objectMapper.writeValueAsString(ucsStudent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String fetchAuthToken() {
        String ucsAuthEndpoint = baseUrl + "/" + authEndpoint;
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

    private UcsStudent fetchStudents(String studentId, String bearerToken) {
        String ucsLicenceEndpoint = baseUrl + "/" + licenceEndpoint + "/" + studentId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<UcsStudent> response = (new RestTemplate()).exchange(ucsLicenceEndpoint, HttpMethod.GET, entity, UcsStudent.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch students", e);
        }
    }
}
