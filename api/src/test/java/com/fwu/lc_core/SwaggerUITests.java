package com.fwu.lc_core;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "PT10S")
class SwaggerUITests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void apiDocsArePubliclyAvailable() {
        webTestClient
                .mutate()
                .responseTimeout(Duration.ofSeconds(10))
                .build()
                .get()
                .uri("/v3/api-docs")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void swaggerUIIsPubliclyAvailable() {
        webTestClient
                .mutate()
                .responseTimeout(Duration.ofSeconds(10))
                .build()
                .get()
                .uri("/swagger-ui/index.html")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void swaggerUIShortHandWorks() {
        webTestClient
                .mutate()
                .responseTimeout(Duration.ofSeconds(10))
                .build()
                .get()
                .uri("/swagger")
                .exchange()
                .expectStatus().isPermanentRedirect()
                .expectHeader().valueEquals("Location", "/swagger-ui/index.html");;
    }
}
