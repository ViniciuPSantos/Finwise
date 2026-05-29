package com.finwise.finwise.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository repo;
    private final long refreshExpirationMs;

    public RefreshTokenService(RefreshTokenRepository repo, @Value("${finwise.jwt.refresh-expiration}") long refreshExpirationMs){
        this.repo = repo;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public RefreshToken create(User user){
        RefreshToken token = new RefreshToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiresAt(Instant.now().plusMillis(refreshExpirationMs));

        return repo.save(token);
    }
}
