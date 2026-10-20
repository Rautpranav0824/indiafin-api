package com.pranav.indiafin_api.ratelimit;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimiterTest {

    @Test
    void rejectsAfterBurstIsUsed() {
        RateLimiter limiter = new RateLimiter(10,5);

        for (int i = 0; i < 10; i++) {
            assertTrue(limiter.tryConsume("client-a").isConsumed());
        }
        assertFalse(limiter.tryConsume("client-a").isConsumed());
    }
}