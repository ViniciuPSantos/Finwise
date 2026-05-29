package com.finwise.finwise.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finwise.finwise.shared.exception.InvalidRefreshTokenException;
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

    public RefreshToken validate(String token){
        RefreshToken refreshToken = repo.findByToken(token)
                .orElseThrow(InvalidRefreshTokenException::new);

        if(refreshToken.isRevoked()){
            throw new InvalidRefreshTokenException();
        }

        if(refreshToken.getExpiresAt().isBefore(Instant.now())){
            throw new InvalidRefreshTokenException();
        }

        return refreshToken;
    }

    public void revoke(String token){
        repo.findByToken(token).ifPresent(refreshToken -> {
            refreshToken.setRevoked(true);
            repo.save(refreshToken);
        });
    }
}
