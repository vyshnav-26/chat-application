package com.chatapp.backend.config;

import com.chatapp.backend.auth.InvalidCredentialsException;
import com.chatapp.backend.auth.InvalidPasswordException;
import com.chatapp.backend.auth.UsernameTakenException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataIntegrityViolationException;

/** Converts validation, framework, and domain exceptions into consistent API errors. */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Formats bean validation failures with per-field messages. */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> fields = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> fields.putIfAbsent(error.getField(), error.getDefaultMessage()));
        ApiError error = new ApiError(Instant.now(), status.value(), "Validation failed", fields);
        return ResponseEntity.status(status).headers(headers).body(error);
    }

    /** Preserves framework HTTP statuses while returning the shared error shape. */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        String message = body instanceof ApiError apiError ? apiError.message()
                : HttpStatus.valueOf(status.value()).getReasonPhrase();
        ApiError error = new ApiError(Instant.now(), status.value(), message, Map.of());
        return ResponseEntity.status(status).headers(headers).body(error);
    }

    /** Converts an explicit HTTP status exception into the standard error response. */
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ApiError> status(ResponseStatusException exception) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        return response(status, exception.getReason() == null ? status.getReasonPhrase() : exception.getReason(), Map.of());
    }

    /** Maps duplicate usernames to HTTP 409. */
    @ExceptionHandler(UsernameTakenException.class)
    ResponseEntity<ApiError> usernameTaken(UsernameTakenException exception) {
        return response(HttpStatus.CONFLICT, exception.getMessage(), Map.of());
    }

    /** Maps invalid login credentials to HTTP 401. */
    @ExceptionHandler(InvalidCredentialsException.class)
    ResponseEntity<ApiError> invalidCredentials(InvalidCredentialsException exception) {
        return response(HttpStatus.UNAUTHORIZED, exception.getMessage(), Map.of());
    }

    /** Maps invalid password input to HTTP 400. */
    @ExceptionHandler(InvalidPasswordException.class)
    ResponseEntity<ApiError> invalidPassword(InvalidPasswordException exception) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage(), Map.of());
    }

    /** Logs unexpected failures and returns a safe HTTP 500 response. */
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> unexpected(Exception exception) {
        log.error("Unhandled server exception", exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", Map.of());
    }

    /** Maps database uniqueness conflicts to HTTP 409. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiError> conflict(DataIntegrityViolationException exception) {
        return response(HttpStatus.CONFLICT, "Resource conflicts with existing data", Map.of());
    }

    private ResponseEntity<ApiError> response(HttpStatus status, String message, Map<String, String> fields) {
        return ResponseEntity.status(status).body(new ApiError(Instant.now(), status.value(), message, fields));
    }
}
