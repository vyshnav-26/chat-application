package com.chatapp.backend.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 32, message = "Username must be between 3 and 32 characters")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username may contain only letters, numbers, and underscores")
        String username,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 72, message = "Password must be at least 8 characters")
        String password) { }
