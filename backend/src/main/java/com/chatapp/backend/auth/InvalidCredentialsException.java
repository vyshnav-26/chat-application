package com.chatapp.backend.auth;

/** Signals that a login attempt has invalid credentials. */
public class InvalidCredentialsException extends RuntimeException {
    /** Creates the exception with a safe client-facing message. */
    public InvalidCredentialsException(String message) { super(message); }
}
