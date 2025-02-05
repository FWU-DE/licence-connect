package com.fwu.lc_core.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ApiTokenAuthenticationFilter extends OncePerRequestFilter {

    private final String theOnlyApiKeyAvailableOnTheSystem;

    public ApiTokenAuthenticationFilter(String theOnlyApiKeyAvailableOnTheSystem) {
        this.theOnlyApiKeyAvailableOnTheSystem = theOnlyApiKeyAvailableOnTheSystem;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String apiKey = extractApiKey(request);
        if (apiKey != null && apiKey.equals(theOnlyApiKeyAvailableOnTheSystem))
            filterChain.doFilter(request, response);
        else
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private static String extractApiKey(HttpServletRequest request) {
        String headerLowerCase = request.getHeader("x-api-key");
        String headerUpperCase = request.getHeader("X-API-KEY");
        return headerLowerCase != null ? headerLowerCase : headerUpperCase;
    }
}
