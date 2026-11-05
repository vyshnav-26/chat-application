package com.chatapp.backend.auth;

import com.chatapp.backend.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    @Operation(summary = "Register an account", description = "Creates an account. Password hashes are never included in the response.")
    public ResponseEntity<CurrentUser> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CurrentUser(user.getId(), user.getUsername()));
    }

    @PostMapping("/login")
    @Operation(summary = "Log in", description = "Returns a bearer JWT when credentials are valid.")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) { return authService.login(request); }
}
