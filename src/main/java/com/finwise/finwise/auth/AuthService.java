package com.finwise.finwise.auth;

import com.finwise.finwise.auth.dto.AuthResponse;
import com.finwise.finwise.auth.dto.RegisterRequest;
import com.finwise.finwise.auth.dto.UserResponse;
import com.finwise.finwise.auth.dto.LoginRequest;
import com.finwise.finwise.auth.dto.RefreshRequest;
import com.finwise.finwise.shared.exception.InvalidCredentialsException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository repo, PasswordEncoder encoder, JwtService jwtService,
            RefreshTokenService refreshTokenService) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public UserResponse register(RegisterRequest request) {
        if (repo.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(encoder.encode(request.password()));

        User saved = repo.save(user);
        return UserResponse.from(saved);
    }

    public AuthResponse login(LoginRequest request) {
        User user = repo.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!encoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtService.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.create(user);

        return AuthResponse.of(accessToken, refreshToken.getToken());
    }

    public AuthResponse refresh(RefreshRequest request){
        RefreshToken refreshToken = refreshTokenService.validate(request.refreshToken());

        String newAccessToken = jwtService.generateToken(refreshToken.getUser().getEmail());

        return AuthResponse.of(newAccessToken, refreshToken.getToken());
    }

    public void logout(RefreshRequest request){
        refreshTokenService.revoke(request.refreshToken());
    }
}