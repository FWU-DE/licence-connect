package com.fwu.lc_core.healthcheck;

import com.fwu.lc_core.config.swagger.SwaggerConfig;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@Tag(
        name = "Healthcheck",
        description = "Check if the application is running and healthy",
        extensions = @Extension(properties = {@ExtensionProperty(name = SwaggerConfig.TAG_ORDER, value = "1")})
)
@RestController
public class HealthCheckController {
    @GetMapping("/v1/healthcheck")
    public ResponseEntity<String> healthcheck() {
        log.debug("Health check request received");
        return ResponseEntity.ok("Application is up");
    }
}
