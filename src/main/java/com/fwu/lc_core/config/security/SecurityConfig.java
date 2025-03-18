package com.fwu.lc_core.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${vidis.api-key.unprivileged}")
    private String unprivilegedApiKey;

    @Value("${vidis.api-key.admin}")
    private String adminApiKey;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        ApiKeyAuthenticationWebFilter apiKeyAuthenticationWebFilter = new ApiKeyAuthenticationWebFilter(unprivilegedApiKey, adminApiKey);

        http.
                csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/swagger", "/swagger-ui/**", "/v3/api-docs/**", "/v1/healthcheck").permitAll()
                        .pathMatchers("/admin/**").hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
                .addFilterAt(apiKeyAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }
}
