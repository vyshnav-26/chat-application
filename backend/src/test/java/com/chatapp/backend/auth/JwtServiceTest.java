package com.chatapp.backend.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chatapp.backend.user.User;
import org.junit.jupiter.api.Test;

class JwtServiceTest {
    private static final String SECRET = "test-secret-that-is-at-least-32-bytes-long";

    @Test
    void rejectsTamperedToken() {
        JwtService service = new JwtService(SECRET, 3600);
        String token = service.generate(new User("alice", "hash"));
        String tampered = token.substring(0, token.length() - 2) + "aa";

        assertThat(service.isValid(tampered)).isFalse();
        assertThatThrownBy(() -> service.parse(tampered)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void rejectsExpiredToken() {
        JwtService service = new JwtService(SECRET, -1);
        String token = service.generate(new User("alice", "hash"));

        assertThat(service.isValid(token)).isFalse();
    }

    @Test
    void rejectsPlaceholderOrShortSigningSecrets() {
        assertThatThrownBy(() -> new JwtService("replace-with-a-random-secret-at-least-32-bytes-long", 3600))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> new JwtService("too-short", 3600))
                .isInstanceOf(IllegalStateException.class);
    }
}
