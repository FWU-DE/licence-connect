package com.fwu.lc_core.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    ApiKeyAuthenticationWebFilter apiKeyAuthenticationWebFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.
                csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/swagger", "/swagger-ui/**", "/v3/api-docs/**", "/v1/healthcheck").permitAll()
                        .pathMatchers("/admin/**").hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
                // This exception handling dance is required so when executing an unauthenticated request in the browser
                // against an endpoint that needs authorization, *no* HTTP Basic auth popup is displayed by the browser.
                .exceptionHandling(spec -> spec
                        .authenticationEntryPoint((exchange, ex) -> {
                                var response = exchange.getResponse();
                                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                                response.getHeaders().set(HttpHeaders.WWW_AUTHENTICATE, "None");
                                exchange.mutate().response(response);
                                return Mono.empty();
                        })
                )
                .addFilterAt(apiKeyAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }
}
