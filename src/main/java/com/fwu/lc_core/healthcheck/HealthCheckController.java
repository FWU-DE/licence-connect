package com.fwu.lc_core.healthcheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    private final Logger logger = LoggerFactory.getLogger(HealthCheckController.class);

    @GetMapping("/v1/healthcheck")
    public ResponseEntity<String> healthcheck() {
        logger.info("Health check request received");
        return ResponseEntity.ok("Application is up");
    }
}
