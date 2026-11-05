package com.chatapp.backend.auth;

/** Login response containing a JWT and its authorization scheme. */
public record AuthResponse(String token, String tokenType) {
    /** Creates a response using the standard Bearer token scheme. */
    public static AuthResponse bearer(String token) { return new AuthResponse(token, "Bearer"); }
}
