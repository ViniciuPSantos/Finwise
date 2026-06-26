package com.finwise.finwise.auth;

import com.finwise.finwise.shared.exception.TooManyRequestsException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_DURATION_MS = 15 * 60 * 1000L;

    private final ConcurrentHashMap<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

    public void checkBlocked(String email) {
        AttemptInfo info = attempts.get(email);
        if (info != null && info.isBlocked()) {
            throw new TooManyRequestsException();
        }
    }

    public void recordFailure(String email) {
        attempts.compute(email, (key, info) -> {
            if (info == null || info.isExpired()) {
                return new AttemptInfo(1, Instant.now());
            }
            return new AttemptInfo(info.count + 1, info.firstAttempt);
        });
    }

    public void recordSuccess(String email) {
        attempts.remove(email);
    }

    private static class AttemptInfo {
        final int count;
        final Instant firstAttempt;

        AttemptInfo(int count, Instant firstAttempt) {
            this.count = count;
            this.firstAttempt = firstAttempt;
        }

        boolean isBlocked() {
            return count >= MAX_ATTEMPTS && !isExpired();
        }

        boolean isExpired() {
            return firstAttempt.plusMillis(BLOCK_DURATION_MS).isBefore(Instant.now());
        }
    }
}
