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
    private String unprivilegedApiKey;

    @Value("${admin.api-key}")
    private String adminApiKey;

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Since the healthcheck endpoint is called every few seconds in production,
        // we skip logging requests to it to reduce logging noise.
        if (exchange.getRequest().getPath().value().equals("/v1/healthcheck")) {
            return chain.filter(exchange);
        }

        var request = exchange.getRequest();

        var headers = request.getHeaders();
        var apiKey = headers.getFirst(API_KEY_HEADER);

        var auth = "None";
        if (apiKey != null) {
            if (apiKey.equals(unprivilegedApiKey)) {
                auth = "Unprivileged";
            } else if (apiKey.equals(adminApiKey)) {
                auth = "Admin";
            }
        }

        logger.info("Request: Id={} Method={}, Path={}, Auth='{}'",
                request.getId(),
                request.getMethod(),
                request.getPath(),
                auth);

        return chain.filter(exchange);
    }
}

