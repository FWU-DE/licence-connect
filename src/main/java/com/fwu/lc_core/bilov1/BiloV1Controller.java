package com.fwu.lc_core.bilov1;

import com.fwu.lc_core.bilov1.DTOs.UcsRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BiloV1Controller {

    @Value("${ucs.api.v1.authentication.endpoint}")
    private String ucsAuthenticationEndpoint;

    @Value("${ucs.api.v1.authentication.admin_username}")
    private String ucsAdminUsername;

    @Value("${ucs.api.v1.authentication.admin_password}")
    private String ucsAdminPassword;

    @PostMapping("/v1/ucs/request")
    private ResponseEntity<Object> request(@RequestBody UcsRequestDto requestDto) {

        String studentId = requestDto.getStudent();

        String ucsJwt = getUcsJwt(studentId);


        return ResponseEntity.ok(null);
    }

    private static String getUcsJwt(String studentId) {
        return "ucs-jwt";
    }
}
