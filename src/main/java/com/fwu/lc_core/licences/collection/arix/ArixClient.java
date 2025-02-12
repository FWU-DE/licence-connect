package com.fwu.lc_core.licences.collection.arix;

import com.fwu.lc_core.licences.models.UnparsedLicences;
import com.fwu.lc_core.shared.Bundesland;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArixClient {
    private String apiUrl;

    public ArixClient(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public Mono<UnparsedLicences> getLicences(Bundesland bundesland, String standortnummer, String schulnummer, String userId) {
        if((bundesland == null && standortnummer != null) | (standortnummer == null && schulnummer != null) | (schulnummer == null && userId != null))
            return Mono.error(new IllegalArgumentException("If you provide a parameter, you must provide all parameters before it."));
        WebClient webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .build();
        String uri = Stream
                .of(bundesland.toString(), standortnummer, schulnummer, userId)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("/"));
        Mono<String> responseMono = webClient.post()
                .uri(uri)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData("xmlstatement", "<search fields='nr, titel'></search>"))
                .exchangeToMono(response -> response.bodyToMono(String.class));


        return responseMono.flatMap(rawResponseBody -> {
            if (!rawResponseBody.startsWith("<result>"))
                return Mono.error(new RuntimeException(rawResponseBody));
            else
                return Mono.just(new UnparsedLicences("ARIX", rawResponseBody));
        });
    }

    private UnparsedLicences getLicencesBlocking(Bundesland bundesland, String standortnummer, String schulnummer, String userId) {
        if((bundesland == null && standortnummer != null) | (standortnummer == null && schulnummer != null) | (schulnummer == null && userId != null))
            throw new IllegalArgumentException("If you provide a parameter, you must provide all parameters before it.");
        WebClient webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .build();
        String uri = Stream
                .of(bundesland.toString(), standortnummer, schulnummer, userId)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("/"));
        String responseBody = webClient.post()
                .uri(uri)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData("xmlstatement", "<search fields='nr, titel'></search>"))
                .exchangeToMono(response -> response.bodyToMono(String.class)).block();

        if(!responseBody.startsWith("<result>"))
            throw new RuntimeException(responseBody);
        return new UnparsedLicences("ARIX", responseBody);
    }
}