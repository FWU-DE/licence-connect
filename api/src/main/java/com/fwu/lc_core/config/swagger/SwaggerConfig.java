package com.fwu.lc_core.config.swagger;

import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

import static com.fwu.lc_core.shared.Constants.API_KEY_HEADER;

@Configuration
public class SwaggerConfig {

    public static final String TAG_ORDER = "x-tag-order";

    @Value("${swagger.target.url}")
    private String targetUrl;

    @Value("${app.version}")
    private String appVersion;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LicenceConnect Core API")
                        .version(appVersion)
                        .description("API for retrieving licences from different licence holding systems in unified ODRL format."))
                .components(new Components()
                        .addSecuritySchemes("apiKey", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(API_KEY_HEADER)
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("apiKey"))
                .servers(List.of(new Server().url(targetUrl)));
    }

    @Bean
    public OpenApiCustomizer sortTags() {
        Comparator<Tag> cmp = Comparator
                .comparingInt(SwaggerConfig::tagOrder)
                .thenComparing(Tag::getName);
        return openApi -> openApi.getTags().sort(cmp);
    }

    private static int tagOrder(Tag tag) {
        return Optional.ofNullable(tag.getExtensions())
                .map(map -> map.get(TAG_ORDER))
                .map(Objects::toString)
                .map(Integer::parseInt)
                .orElse(Integer.MAX_VALUE);
    }
}
