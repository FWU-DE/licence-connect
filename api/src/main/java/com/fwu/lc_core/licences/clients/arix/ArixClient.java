package com.fwu.lc_core.licences.clients.arix;

import com.fwu.lc_core.licences.models.ODRLPolicy;
import com.fwu.lc_core.shared.Bundesland;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ArixClient {
    private final String apiUrl;

    public ArixClient(@Value("${arix.accepting.url}") String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public Mono<List<ODRLPolicy.Permission>> getPermissions(Bundesland bundesland, String standortnummer, String schulnummer, String userId) {
        try {
            assertParametersAreValid(bundesland, standortnummer, schulnummer, userId);
        } catch (IllegalArgumentException e) {
            return Mono.error(e);
        }

        WebClient webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .build();

        String uri = Stream
                .of(bundesland.toString(), standortnummer, schulnummer, userId)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("/"));

        return webClient.post()
                .uri(uri)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData("xmlstatement", "<search></search>"))
                .exchangeToMono(response -> response.bodyToMono(String.class))
                .handle((responseBody, sink) -> {
                    if (responseBody == null || !responseBody.startsWith("<result")) {
                        sink.error(new RuntimeException(responseBody));
                        return;
                    }

                    try {
                        sink.next(ArixParser.parse(responseBody));
                    } catch (Exception e) {
                        sink.error(e);
                    }
                });
    }

    private static void assertParametersAreValid(Bundesland bundesland, String standortnummer, String schulnummer, String userId) {
        if (bundesland == null) {
            throw new IllegalArgumentException("You must provide a Bundesland.");
        }

        if ((isNullOrEmpty(standortnummer) && isNotNullOrEmpty(schulnummer))) {
            throw new IllegalArgumentException("If schulnummer is provided, standortnummer must also be provided.");
        }

        if ((isNullOrEmpty(schulnummer) && isNotNullOrEmpty(userId))) {
            throw new IllegalArgumentException("If userId is provided, schulnummer must also be provided.");
        }
    }

    private static Boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private static Boolean isNotNullOrEmpty(String str) {
        return !isNullOrEmpty(str);
    }
}

