package com.chatapp.backend.auth;

import com.chatapp.backend.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Creates and validates signed JWTs for authenticated users. */
@Service
public class JwtService {
    private final SecretKey key;
    private final long expirationSeconds;

    /** Creates a JWT service from a secret and token lifetime in seconds. */
    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-seconds:3600}") long expirationSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
    }

    /** Generates a signed token containing the user's id and username. */
    public String generate(User user) {
        Instant now = Instant.now();
        return Jwts.builder().subject(user.getUsername()).claim("uid", user.getId())
                .issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(key).compact();
    }

    /** Parses a signed token and returns its claims, rejecting invalid or expired tokens. */
    public Claims parse(String token) throws JwtException {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    /** Returns whether a token has a valid signature and has not expired. */
    public boolean isValid(String token) {
        try { parse(token); return true; } catch (JwtException | IllegalArgumentException exception) { return false; }
    }
}
