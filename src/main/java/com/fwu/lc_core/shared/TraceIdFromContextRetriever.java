package com.fwu.lc_core.shared;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
public class TraceIdFromContextRetriever {
    public static  <T> Mono<T>  withLoggingContext(Callable<Mono<T>> callable) {
        return Mono.deferContextual(contextView -> {
            try {
                return callable.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).doFinally(signalType -> MDC.clear());
    }
}
