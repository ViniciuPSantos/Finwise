package com.finwise.finwise.auth;

import com.finwise.finwise.auth.dto.RegisterRequest;
import com.finwise.finwise.auth.dto.UserResponse;
import com.finwise.finwise.auth.dto.AuthResponse;
import com.finwise.finwise.auth.dto.LoginRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController{
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request){
        UserResponse response = service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        AuthResponse response = service.login(request);
        return ResponseEntity.ok(response);
    }
}