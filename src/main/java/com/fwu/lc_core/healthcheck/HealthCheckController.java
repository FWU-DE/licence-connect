package com.fwu.lc_core.healthcheck;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HealthCheckController {
    @GetMapping("/v1/healthcheck")
    public ResponseEntity<String> healthcheck() {
        log.debug("Health check request received");
        return ResponseEntity.ok("Application is up");
    }
}
