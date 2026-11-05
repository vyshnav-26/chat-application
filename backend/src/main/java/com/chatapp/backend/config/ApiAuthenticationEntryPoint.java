package com.chatapp.backend.config;

import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/** Writes unauthenticated API requests as standard JSON 401 responses. */
@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    /** Creates the entry point using the application's configured JSON mapper. */
    public ApiAuthenticationEntryPoint(ObjectMapper objectMapper) { this.objectMapper = objectMapper; }

    /** Sends a standard unauthorized response without exposing authentication details. */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), new ApiError(
                Instant.now(), HttpStatus.UNAUTHORIZED.value(), "Authentication is required", Map.of()));
    }
}
