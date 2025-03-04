package com.fwu.lc_core.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

import static com.fwu.lc_core.shared.Constants.API_KEY_HEADER;

public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final String unprivilegedApiKey;
    private final String adminApiKey;

    public ApiKeyAuthenticationFilter(
            String unprivilegedApiKey,
            String adminApiKey
    ) {
        this.unprivilegedApiKey = unprivilegedApiKey;
        this.adminApiKey = adminApiKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String apiKey = request.getHeader(API_KEY_HEADER);
        if (apiKey != null && (apiKey.equals(this.unprivilegedApiKey) || apiKey.equals(this.adminApiKey))) {
            SecurityContextHolder.getContext().setAuthentication(new ApiKeyAuthenticationToken(apiKey, getAuthoritiesForApiKey(apiKey)));
        }
        filterChain.doFilter(request, response);
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
