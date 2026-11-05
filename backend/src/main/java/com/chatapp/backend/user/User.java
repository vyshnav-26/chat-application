package com.chatapp.backend.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** JPA account entity mapped to the users table. */
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** Required by JPA. */
    protected User() { }

    /** Creates a new account with its already-hashed password. */
    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = Instant.now();
    }

    /** Returns the persisted account id. */
    public Long getId() { return id; }
    /** Returns the unique username. */
    public String getUsername() { return username; }
    /** Returns the BCrypt password hash for authentication checks. */
    public String getPasswordHash() { return passwordHash; }
    /** Returns the account creation timestamp. */
    public Instant getCreatedAt() { return createdAt; }
}
