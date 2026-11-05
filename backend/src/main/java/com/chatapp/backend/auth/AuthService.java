package com.chatapp.backend.auth;

import com.chatapp.backend.user.User;
import com.chatapp.backend.user.UserStore;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/** Applies account creation and credential authentication rules. */
@Service
public class AuthService {
    private final UserStore users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /** Creates the service with user storage, password hashing, token, and credential services. */
    public AuthService(UserStore users, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /** Creates an account after validating the BCrypt input limit and username uniqueness. */
    @Transactional
    public User register(RegisterRequest request) {
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new InvalidPasswordException("Password must not exceed 72 bytes in UTF-8");
        }
        if (users.existsByUsername(request.username())) {
            throw new UsernameTakenException("Username is already taken");
        }
        return users.save(new User(request.username(), passwordEncoder.encode(request.password())));
    }

    /** Verifies credentials and returns a signed bearer token. */
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (org.springframework.security.core.AuthenticationException exception) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
        User user = users.findByUsername(request.username()).orElseThrow(
                () -> new InvalidCredentialsException("Invalid username or password"));
        return AuthResponse.bearer(jwtService.generate(user));
    }
}
