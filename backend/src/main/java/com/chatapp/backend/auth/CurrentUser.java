package com.chatapp.backend.auth;

/** Public representation of an account that excludes its password hash. */
public record CurrentUser(Long id, String username) { }
