package com.fwu.lc_core;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
public class SecurityConfigTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void wwwAuthenticateHeaderIsSetToNoneForUnauthorizedRequests() {
        webTestClient.get()
                .uri("/v1/licences/request")
                .exchange()
                .expectHeader().valueEquals("WWW-Authenticate", "None")
                .expectStatus().isUnauthorized();
    }
}
