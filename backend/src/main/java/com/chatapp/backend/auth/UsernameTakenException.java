package com.chatapp.backend.auth;

/** Signals that a requested username is already registered. */
public class UsernameTakenException extends RuntimeException {
    /** Creates the exception with a client-facing conflict message. */
    public UsernameTakenException(String message) { super(message); }
}
