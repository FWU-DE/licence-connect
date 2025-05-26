package com.fwu.lc_core;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
class SwaggerUITests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void apiDocsArePubliclyAvailable() {
        webTestClient
                .get()
                .uri("/v3/api-docs")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void swaggerUIIsPubliclyAvailable() {
        webTestClient
                .get()
                .uri("/swagger-ui/index.html")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void swaggerUIShortHandWorks() {
        webTestClient
                .get()
                .uri("/swagger")
                .exchange()
                .expectStatus().isPermanentRedirect()
                .expectHeader().valueEquals("Location", "/swagger-ui/index.html");;
    }
}
