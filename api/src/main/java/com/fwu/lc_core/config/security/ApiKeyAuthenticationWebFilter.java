package com.fwu.lc_core.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collection;

import static com.fwu.lc_core.shared.Constants.API_KEY_HEADER;

@Component
public class ApiKeyAuthenticationWebFilter implements WebFilter {

    @Value("${vidis.api-key.unprivileged}")
    private String unprivilegedApiKey;

    @Value("${admin.api-key}")
    private String adminApiKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String apiKey = exchange.getRequest().getHeaders().getFirst(API_KEY_HEADER);
        if (apiKey != null && (apiKey.equals(this.unprivilegedApiKey) || apiKey.equals(this.adminApiKey))) {
            ApiKeyAuthenticationToken authentication = new ApiKeyAuthenticationToken(apiKey, getAuthoritiesForApiKey(apiKey));
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        }
        return chain.filter(exchange);
    }

    private Collection<? extends GrantedAuthority> getAuthoritiesForApiKey(String apiKey) {
        if (adminApiKey.equals(apiKey)) {
            return AuthorityUtils.createAuthorityList("ROLE_ADMIN");
        }
        return AuthorityUtils.NO_AUTHORITIES;
    }
}

class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {
    private final String apiKey;

    public ApiKeyAuthenticationToken(String apiKey, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.apiKey = apiKey;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return apiKey;
    }
}
