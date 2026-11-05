package com.chatapp.backend.auth;

import com.chatapp.backend.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Exposes public account registration and login endpoints. */
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    /** Creates the controller with its authentication service. */
    public AuthController(AuthService authService) { this.authService = authService; }

    /** Registers a validated user and returns only the public account fields. */
    @PostMapping("/register")
    public ResponseEntity<CurrentUser> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CurrentUser(user.getId(), user.getUsername()));
    }

    /** Authenticates credentials and returns a bearer token. */
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) { return authService.login(request); }
}
