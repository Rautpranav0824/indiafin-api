package com.pranav.indiafin_api.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class TokenBucket {

    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket newBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(10)
                .refillGreedy(5, Duration.ofSeconds(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    public ConsumptionProbe tryConsume(String clientId) {
        return buckets.computeIfAbsent(clientId, id -> newBucket())
                .tryConsumeAndReturnRemaining(1);
    }
}


