package com.io.github.cawodevelopment.ratelimiter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;

    private final int MAX_REQUESTS;

    private final int WINDOW_SECONDS;

    public RateLimiterService(StringRedisTemplate redisTemplate,
                              @Value("${env.max-requests}") int maxRequests,
                              @Value("${env.window-seconds}") int windowSeconds) {
        this.redisTemplate = redisTemplate;
        MAX_REQUESTS = maxRequests;
        WINDOW_SECONDS = windowSeconds;
    }

    public boolean isAllowed(String clientIp) {
        String key = "rate_limit:" + clientIp;

        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null && count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(WINDOW_SECONDS));
        }

        return count != null && count <= MAX_REQUESTS;
    }
}
