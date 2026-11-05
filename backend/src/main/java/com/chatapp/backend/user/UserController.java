package com.chatapp.backend.user;

import com.chatapp.backend.auth.CurrentUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("/users/me")
    public CurrentUser me(@AuthenticationPrincipal User user) {
        return new CurrentUser(user.getId(), user.getUsername());
    }
}
