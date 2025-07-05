package com.chatbot.chatbotservice.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.chatbot.chatbotservice.security.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private final ApiKeyValidator apiKeyValidator;

    public ApiKeyFilter(ApiKeyValidator apiKeyValidator) {
        this.apiKeyValidator = apiKeyValidator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String apiKey = request.getHeader("X-API-KEY");

        if (!apiKeyValidator.isValid(apiKey)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            Map<String, Object> errorBody = Map.of(
                    "status", 401,
                    "error", "Unauthorized",
                    "message", "Invalid or missing API Key"
            );
            new ObjectMapper().writeValue(response.getWriter(), errorBody);
            return;
        }

        filterChain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/actuator");
    }

}
