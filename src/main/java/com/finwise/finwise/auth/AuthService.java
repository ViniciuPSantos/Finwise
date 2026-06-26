package com.finwise.finwise.auth;

import com.finwise.finwise.auth.dto.AuthResponse;
import com.finwise.finwise.auth.dto.RegisterRequest;
import com.finwise.finwise.auth.dto.UserResponse;
import com.finwise.finwise.auth.dto.LoginRequest;
import com.finwise.finwise.auth.dto.RefreshRequest;
import com.finwise.finwise.category.Category;
import com.finwise.finwise.category.CategoryRepository;
import com.finwise.finwise.shared.exception.DuplicateEmailException;
import com.finwise.finwise.shared.exception.InvalidCredentialsException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {
    private static final List<String> DEFAULT_CATEGORIES = List.of(
            "Alimentação", "Transporte", "Saúde", "Moradia",
            "Lazer", "Educação", "Salário", "Outros");

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CategoryRepository categoryRepository;
    private final LoginAttemptService loginAttemptService;

    public AuthService(UserRepository repo, PasswordEncoder encoder, JwtService jwtService,
            RefreshTokenService refreshTokenService, CategoryRepository categoryRepository,
            LoginAttemptService loginAttemptService) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.categoryRepository = categoryRepository;
        this.loginAttemptService = loginAttemptService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (repo.existsByEmail(request.email())) {
            throw new DuplicateEmailException("Email already registered");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(encoder.encode(request.password()));

        User saved = repo.save(user);

        DEFAULT_CATEGORIES.forEach(name -> {
            Category category = new Category();
            category.setName(name);
            category.setUser(saved);
            categoryRepository.save(category);
        });

        return UserResponse.from(saved);
    }

    public AuthResponse login(LoginRequest request) {
        loginAttemptService.checkBlocked(request.email());

        User user = repo.findByEmail(request.email()).orElse(null);
        if (user == null || !encoder.matches(request.password(), user.getPassword())) {
            loginAttemptService.recordFailure(request.email());
            throw new InvalidCredentialsException();
        }

        loginAttemptService.recordSuccess(request.email());
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