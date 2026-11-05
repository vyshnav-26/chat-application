package com.chatapp.backend.user;

import java.util.Optional;

/** Minimal persistence operations required by the authentication service. */
public interface UserStore {
    /** Finds one account by its unique username. */
    Optional<User> findByUsername(String username);
    /** Returns whether the username is already assigned to an account. */
    boolean existsByUsername(String username);
    /** Persists an account and returns its saved representation. */
    User save(User user);
}
