package com.fwu.lc_core.licences.clients.lcHalt;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fwu.lc_core.licences.models.ODRLPolicy;
import com.fwu.lc_core.shared.Bundesland;
import static com.fwu.lc_core.shared.Constants.API_KEY_HEADER;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnProperty(name="lc-halt.enabled", havingValue="true")
@Slf4j
public class LCHaltClient {
    @Value("${lc-halt.licence-url}")
    private String licenceUrl;

    @Value("${lc-halt.client-api-key}")
    private String lcHaltClientApiKey;

    public Mono<List<ODRLPolicy.Permission>> getPermissions(Bundesland bundesland, String standortnummer, String schulnummer) {
        try {
            assertParametersAreValid(bundesland, standortnummer, schulnummer);
        } catch (IllegalArgumentException e) {
            return Mono.error(e);
        }

        WebClient webClient = WebClient.builder()
                .baseUrl(licenceUrl)
                .build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("bundesland_id", bundesland.toISOIdentifier())
                        .queryParamIfPresent("landkreis_id", Optional.ofNullable(standortnummer))
                        .queryParamIfPresent("schul_id", Optional.ofNullable(schulnummer))
                        .build())
                .header(API_KEY_HEADER, lcHaltClientApiKey)
                .exchangeToMono(response -> response.bodyToMono(String.class))
                .handle((responseBody, sink) -> {
                    if (responseBody == null) {
                        sink.error(new RuntimeException("Response body is null"));
                        return;
                    }

                    try {
                        sink.next(LCHaltParser.parse(responseBody));
                    } catch (Exception e) {
                        sink.error(e);
                    }
                });
    }

    private static void assertParametersAreValid(Bundesland bundesland, String standortnummer, String schulnummer) {
        if (bundesland == null) {
            throw new IllegalArgumentException("Bundesland is required.");
        }

        if (bundesland != Bundesland.BB && standortnummer == null && isNotNullOrEmpty(schulnummer)) {
            throw new IllegalArgumentException("If schulnummer is provided, standortnummer must also be provided.");
        }
    }

    private static Boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private static Boolean isNotNullOrEmpty(String str) {
        return !isNullOrEmpty(str);
    }
}

