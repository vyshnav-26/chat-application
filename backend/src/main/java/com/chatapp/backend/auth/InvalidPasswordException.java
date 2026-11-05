package com.chatapp.backend.auth;

/** Signals that a submitted password violates authentication rules. */
public class InvalidPasswordException extends RuntimeException {
    /** Creates the exception with a client-facing validation message. */
    public InvalidPasswordException(String message) { super(message); }
}
