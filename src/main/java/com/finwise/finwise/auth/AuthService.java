package com.finwise.finwise.auth;

import com.finwise.finwise.auth.dto.AuthResponse;
import com.finwise.finwise.auth.dto.RegisterRequest;
import com.finwise.finwise.auth.dto.UserResponse;
import com.finwise.finwise.auth.dto.LoginRequest;
import com.finwise.finwise.shared.exception.InvalidCredentialsException;

import com.finwise.finwise.auth.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService{
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthService(UserRepository repo, PasswordEncoder encoder, JwtService jwtService){
        this.repo = repo;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest request){
        if(repo.existsByEmail(request.email())){
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(encoder.encode(request.password()));

        User saved = repo.save(user);
        return UserResponse.from(saved);
    }

    public AuthResponse login(LoginRequest request){
        User user = repo.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

                if(!encoder.matches(request.password(), user.getPassword())){
                    throw new InvalidCredentialsException();
                }

                String token = jwtService.generateToken(user.getEmail());
                return AuthResponse.bearer(token);
    }
}