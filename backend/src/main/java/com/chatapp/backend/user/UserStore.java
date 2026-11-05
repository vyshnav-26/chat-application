package com.chatapp.backend.user;

import java.util.Optional;

public interface UserStore {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    User save(User user);
}
