package com.fwu.lc_core.healthcheck;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
public class HealthCheckController {
    @GetMapping("/v1/healthcheck")
    public ResponseEntity<String> healthcheck() {
        return ResponseEntity.ok("Application is up");
    }
}
