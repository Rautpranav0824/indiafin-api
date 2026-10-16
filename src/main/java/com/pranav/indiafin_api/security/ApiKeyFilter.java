package com.pranav.indiafin_api.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private final String validKey;

    public ApiKeyFilter(@Value("${indiafin.api-key}") String validKey) {
        this.validKey = validKey;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().equals("/ping");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String sentKey = request.getHeader("X-API-Key");

        if (sentKey == null || !MessageDigest.isEqual(
                sentKey.getBytes(StandardCharsets.UTF_8),
                validKey.getBytes(StandardCharsets.UTF_8))) {
            response.setStatus(401);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\","
                    + "\"message\":\"Missing or invalid API key\",\"timestamp\":\""
                    + Instant.now() + "\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}