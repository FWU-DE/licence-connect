package com.fwu.lc_core.config.logging;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class TraceIdFilterWebflux implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String traceId = UUID.randomUUID().toString();
        return chain.filter(exchange)
                .contextWrite(context -> context.put("traceId", traceId))
                .doFinally(signalType -> MDC.clear());
    }
}
