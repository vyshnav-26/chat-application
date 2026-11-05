package com.chatapp.backend.auth;

import jakarta.validation.constraints.NotBlank;

/** Credentials submitted to the login endpoint. */
public record LoginRequest(@NotBlank String username, @NotBlank String password) { }
