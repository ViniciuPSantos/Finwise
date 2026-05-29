package com.finwise.finwise.auth;

import com.finwise.finwise.auth.dto.UserResponse;
import com.finwise.finwise.shared.exception.InvalidCredentialsException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
public class MeController {
    private final UserRepository repo;

    public MeController(UserRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal String email){
        User user = repo.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
        return ResponseEntity.ok(UserResponse.from(user));
    }
}
