package com.pranav.indiafin_api.security;

import com.pranav.indiafin_api.ratelimit.RateLimiter;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Component
@Order(2)
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiter rateLimiter;

    public RateLimitFilter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().equals("/ping");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String clientId = request.getHeader("X-RapidAPI-User");
        if (clientId == null || clientId.isBlank()) {
            clientId = request.getRemoteAddr();
        }

        ConsumptionProbe probe = rateLimiter.tryConsume(clientId);

        if (probe.isConsumed()) {
            chain.doFilter(request, response);
            return;
        }

        long retryAfterSeconds = Math.max(1, (long) Math.ceil(probe.getNanosToWaitForRefill() / 1_000_000_000.0));

        response.setStatus(429);
        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("{\"status\":429,\"error\":\"Too Many Requests\","
                + "\"message\":\"Rate limit exceeded\",\"timestamp\":\"" + Instant.now() + "\"}");
    }
}