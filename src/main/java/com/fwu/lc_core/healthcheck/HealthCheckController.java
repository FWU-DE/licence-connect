package com.fwu.lc_core.healthcheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @GetMapping("/v1/healthcheck")
    public ResponseEntity<String> healthcheck() {
        Logger logger = LoggerFactory.getLogger(HealthCheckController.class);
        logger.info("Health check request received");
        logger.error("Health check request received");
        return ResponseEntity.ok("Application is up");
    }
}
