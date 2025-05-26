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

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);  // Logger instance for logging.

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var request = exchange.getRequest();

        var headers = request.getHeaders();
        var apiKey = headers.get(API_KEY_HEADER).toString();
        headers.remove(API_KEY_HEADER);

        var auth = "None";
        if (apiKey.equals(unprivilegedApiKey)) {
            auth = "Unprivileged";
        } else if (apiKey.equals(adminApiKey)) {
            auth = "Admin";
        }

        logger.info("Request: Id={} Method={}, Path={}, Auth='{}', Headers={}",
                request.getId(),
                request.getMethod(),
                request.getPath(),
                auth,
                headers);

        return chain.filter(exchange);
    }
}

