package com.fwu.lc_core.licences.clients.arix;

import com.fwu.lc_core.licences.models.ODRLPolicy;
import com.fwu.lc_core.shared.Bundesland;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ArixClient {
    private final String apiUrl;

    public ArixClient(@Value("${arix.url}") String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public Mono<List<ODRLPolicy.Permission>> getPermissions(Bundesland bundesland, String standortnummer, String schulnummer, String userId) {
        String uri;
        try {
            uri = constructRequestUri(bundesland, standortnummer, schulnummer, userId);
        } catch (IllegalArgumentException e) {
            return Mono.error(e);
        }
        
        WebClient webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .build();

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

    private static String constructRequestUri(Bundesland bundesland, String standortnummer, String schulnummer, String userId) {
        if (bundesland == null) {
            throw new IllegalArgumentException("You must provide a Bundesland.");
        }
        if (isNullOrEmpty(standortnummer)) {
            return bundesland.value;
        }
        if (isNullOrEmpty(schulnummer)) {
            return Stream
                .of(bundesland.toString(), standortnummer)
                .collect(Collectors.joining("/"));
        }
        if (isNullOrEmpty(userId)) {
            return Stream
                .of(bundesland.toString(), standortnummer, schulnummer)
                .collect(Collectors.joining("/"));     
        }
        return Stream
            .of(bundesland.toString(), standortnummer, schulnummer, userId)
            .collect(Collectors.joining("/"));
    }


    private static Boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}

