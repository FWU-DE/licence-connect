package com.fwu.lc_core.licences.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fwu.lc_core.licences.models.LicenceHolder;
import com.fwu.lc_core.licences.models.ODRLAction;
import com.fwu.lc_core.licences.models.ODRLLicenceResponse;
import com.fwu.lc_core.shared.Bundesland;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.fwu.lc_core.shared.Constants.API_KEY_HEADER;

@Component
public class LCHaltClient {
    @Value("${lc-halt.licence-url}")
    private String licenceUrl;

    @Value("${lc-halt.client-api-key}")
    private String lcHaltClientApiKey;

    public Mono<List<ODRLLicenceResponse.Permission>> getPermissions(Bundesland bundesland, String standortnummer, String schulnummer, String userId) {
        return Mono.fromCallable(() -> getPermissionsBlocking(bundesland, standortnummer, schulnummer, userId)).subscribeOn(Schedulers.boundedElastic());
    }

    private List<ODRLLicenceResponse.Permission> getPermissionsBlocking(Bundesland bundesland, String standortnummer, String schulnummer, String userId) {
        validateParameters(bundesland, standortnummer, schulnummer, userId);

        WebClient webClient = WebClient.builder()
                .baseUrl(licenceUrl)
                .build();

        String responseBody = webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .queryParam("userId", userId)
                    .queryParamIfPresent("bundesland", Optional.ofNullable(bundesland))
                    .queryParamIfPresent("schulnummer", Optional.ofNullable(schulnummer))
                    .build())
                .header(API_KEY_HEADER, lcHaltClientApiKey)
                .exchangeToMono(response -> response.bodyToMono(String.class)).block();

        return LCHaltParser.parse(responseBody);
    }

    private static void validateParameters(Bundesland bundesland, String standortnummer, String schulnummer, String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("A non-empty userId parameter is required.");
        }
    }
}

@Slf4j
class LCHaltParser {
    static List<ODRLLicenceResponse.Permission> parse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            var json = objectMapper.readTree(responseBody);
            var licencedMedia = json.path("licencedMedia");
            if (licencedMedia.isMissingNode() || !licencedMedia.isArray()) {
                throw new RuntimeException("Invalid response format: 'licencedMedia' is missing or not an array.");
            }

            List<ODRLLicenceResponse.Permission> permissions = new ArrayList<>();
            for (var media : licencedMedia) {
                permissions.add(new ODRLLicenceResponse.Permission(media.path("id").textValue(), LicenceHolder.LC_HALT, ODRLAction.Use));
            }
            log.info("Found {} licences on `{}`", permissions.size(), LicenceHolder.LC_HALT);
            return permissions;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse responseBody as JSON", e);
        }
    }
}
