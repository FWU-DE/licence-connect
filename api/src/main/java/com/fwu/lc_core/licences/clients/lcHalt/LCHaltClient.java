package com.fwu.lc_core.licences.clients.lcHalt;

import com.fwu.lc_core.licences.models.ODRLPolicy;
import com.fwu.lc_core.shared.Bundesland;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;

import static com.fwu.lc_core.shared.Constants.API_KEY_HEADER;

@Component
public class LCHaltClient {
    @Value("${lc-halt.licence-url}")
    private String licenceUrl;

    @Value("${lc-halt.client-api-key}")
    private String lcHaltClientApiKey;

    public Mono<List<ODRLPolicy.Permission>> getPermissions(Bundesland bundesland, String standortnummer, String schulnummer, String userId) {
        try {
            assertParametersAreValid(bundesland, standortnummer, schulnummer, userId);
        } catch (IllegalArgumentException e) {
            return Mono.error(e);
        }

        WebClient webClient = WebClient.builder()
                .baseUrl(licenceUrl)
                .build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("user_id", userId)
                        .queryParamIfPresent("bundesland_id", Optional.ofNullable(bundesland))
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

    private static void assertParametersAreValid(Bundesland bundesland, String standortnummer, String schulnummer, String userId) {
        if (isNullOrEmpty(userId)) {
            throw new IllegalArgumentException("You must provide a userId parameter.");
        }

        if (bundesland == null && isNotNullOrEmpty(schulnummer)) {
            throw new IllegalArgumentException("If schulnummer is provided, bundesland must also be provided.");
        }
    }

    private static Boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private static Boolean isNotNullOrEmpty(String str) {
        return !isNullOrEmpty(str);
    }
}

