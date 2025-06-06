package com.fwu.lc_core.config.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static com.fwu.lc_core.shared.Constants.API_KEY_HEADER;

@Component
public class LoggingFilter implements WebFilter {

    @Value("${vidis.api-key.unprivileged}")
    private String vidisApiKey;

    @Value("${admin.api-key}")
    private String adminApiKey;

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        final var request = exchange.getRequest();

        // Since the healthcheck endpoint is called every few seconds in production,
        // we skip logging requests to it to reduce logging noise.
        if (request.getPath().value().equals("/v1/healthcheck")) {
            return chain.filter(exchange);
        }
        
        final var apiKey = request.getHeaders().getFirst(API_KEY_HEADER);
        final var auth = getAuthenticationType(apiKey);

        logger.info("Request: Id={} Method={}, Path={}, Auth='{}'",
                request.getId(),
                request.getMethod(),
                request.getPath(),
                auth);

        return chain.filter(exchange);
    }

    private String getAuthenticationType(String apiKey) {
        if (apiKey == null) {
            return "No API Key";
        } else if (apiKey.equals(vidisApiKey)) {
            return "VIDIS API Key";
        } else if (apiKey.equals(adminApiKey)) {
            return "Admin API Key";
        }
        return "Unknown API Key";
    }
}

