package com.fwu.lc_core.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ApiTokenAuthenticationFilter extends OncePerRequestFilter {

    private final String theOnlyApiKeyAvailableOnTheSystem;

    public ApiTokenAuthenticationFilter(
            String theOnlyApiKeyAvailableOnTheSystem
    ) {
        this.theOnlyApiKeyAvailableOnTheSystem = theOnlyApiKeyAvailableOnTheSystem;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("I do internal Filtering!");
        String apiKey = extractApiKey(request);
        if (apiKey != null && apiKey.equals(theOnlyApiKeyAvailableOnTheSystem)) {
            System.out.println("yay api key is correct!");
            filterChain.doFilter(request, response);
        }
        else {
            System.out.println("nay api key is incorrect!");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    private String extractApiKey(HttpServletRequest request) {
        String apiKey = request.getHeader("x-api-key");
        if (apiKey == null) {
            apiKey = request.getHeader("X-API-KEY");
        }
        return apiKey;
    }
}
