package com.chatapp.backend.config;

import java.time.Instant;
import java.util.Map;

/** Standard response body returned for API errors. */
public record ApiError(Instant timestamp, int status, String message, Map<String, String> fieldErrors) { }
