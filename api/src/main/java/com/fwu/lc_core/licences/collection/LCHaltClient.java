package com.fwu.lc_core.licences.collection;

import com.fwu.lc_core.licences.models.LicenceHolder;
import com.fwu.lc_core.licences.models.UnparsedLicences;
import com.fwu.lc_core.shared.Bundesland;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

import static com.fwu.lc_core.shared.Constants.API_KEY_HEADER;

public class LCHaltClient {
    private final String apiUrl;

    public LCHaltClient(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Value("${lcHalt.clientApiKey}")
    private String lcHaltClientApiKey;

    public Mono<UnparsedLicences> getLicences(String userId, Bundesland bundesland, String schulnummer) {
        return Mono.fromCallable(() -> getLicencesBlocking(userId, bundesland, schulnummer)).subscribeOn(Schedulers.boundedElastic());
    }

    private UnparsedLicences getLicencesBlocking(String userId, Bundesland bundesland, String schulnummer) {
        if (userId == null)
            throw new IllegalArgumentException("A non-empty userId parameter is required.");

        WebClient webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .build();

        String responseBody = webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/licences/")
                    .queryParam("userId", userId)
                    .queryParamIfPresent("bundesland", Optional.ofNullable(bundesland))
                    .queryParamIfPresent("schulnummer", Optional.ofNullable(schulnummer))
                    .build())
                .header(API_KEY_HEADER, lcHaltClientApiKey)
                .exchangeToMono(response -> response.bodyToMono(String.class)).block();

        return new UnparsedLicences(LicenceHolder.LC_HALT, responseBody);
    }
}
