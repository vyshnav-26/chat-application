package com.chatapp.backend.user;

import com.chatapp.backend.auth.CurrentUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

/** Exposes authenticated account details. */
@RestController
public class UserController {
    /** Returns the current authenticated user's public account fields. */
    @GetMapping("/users/me")
    public CurrentUser me(@AuthenticationPrincipal User user) {
        return new CurrentUser(user.getId(), user.getUsername());
    }
}
