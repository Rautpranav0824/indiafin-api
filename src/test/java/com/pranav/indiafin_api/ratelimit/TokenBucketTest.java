package com.pranav.indiafin_api.ratelimit;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenBucketTest {

    @Test
    void rejectsAfterBurstIsUsed() {
        TokenBucket limiter = new TokenBucket();

        for (int i = 0; i < 10; i++) {
            assertTrue(limiter.tryConsume("client-a").isConsumed());
        }
        assertFalse(limiter.tryConsume("client-a").isConsumed());
    }
}