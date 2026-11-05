package com.chatapp.backend.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data access methods for user accounts. */
public interface UserRepository extends JpaRepository<User, Long>, UserStore {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
