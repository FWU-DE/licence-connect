package com.fwu.lc_core;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
class HealthCheckTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void requestWithoutApiKey() {
        webTestClient
                .get()
                .uri("/v1/healthcheck")
                .exchange()
                .expectStatus().isOk();
    }
}
