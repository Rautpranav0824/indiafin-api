package com.pranav.indiafin_api.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class RateLimiter {

    private final long capacity;
    private final long refillPerSecond;
    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public RateLimiter(@Value("${indiafin.rate-limit.capacity:10}") long capacity,
                       @Value("${indiafin.rate-limit.refill-per-second:5}") long refillPerSecond) {
        this.capacity = capacity;
        this.refillPerSecond = refillPerSecond;
    }

    private Bucket newBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(refillPerSecond, Duration.ofSeconds(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    public ConsumptionProbe tryConsume(String clientId) {
        return buckets.computeIfAbsent(clientId, id -> newBucket())
                .tryConsumeAndReturnRemaining(1);
    }
}

