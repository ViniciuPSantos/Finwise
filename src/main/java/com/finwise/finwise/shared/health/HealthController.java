package com.finwise.finwise.shared.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public  Map<String, Object> health(){
        return Map.of(
                "status", "UP",
                "service", "finwise",
                "timestamp", Instant.now().toString()
        );
    }
}
