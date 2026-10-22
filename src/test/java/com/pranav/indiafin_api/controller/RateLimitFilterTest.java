package com.pranav.indiafin_api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "indiafin.api-key=test-key",
        "indiafin.rate-limit.capacity=3",
        "indiafin.rate-limit.refill-per-second=1"
})
@AutoConfigureMockMvc
class RateLimitFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fourthRequestIsRejectedWith429() throws Exception {
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/pan/ABCPE1234F")
                            .header("X-API-Key", "test-key")
                            .header("X-RapidAPI-User", "user-a"))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/pan/ABCPE1234F")
                        .header("X-API-Key", "test-key")
                        .header("X-RapidAPI-User", "user-a"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("Retry-After", "1"))
                .andExpect(jsonPath("$.message").value("Rate limit exceeded"));
    }

    @Test
    void secondUserIsNotAffectedByTheFirst() throws Exception {
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/pan/ABCPE1234F")
                            .header("X-API-Key", "test-key")
                            .header("X-RapidAPI-User", "user-c"))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/pan/ABCPE1234F")
                        .header("X-API-Key", "test-key")
                        .header("X-RapidAPI-User", "user-c"))
                .andExpect(status().isTooManyRequests());

        mockMvc.perform(get("/pan/ABCPE1234F")
                        .header("X-API-Key", "test-key")
                        .header("X-RapidAPI-User", "user-d"))
                .andExpect(status().isOk());
    }

    @Test
    void pingIsNeverRateLimited() throws Exception {
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(get("/ping"))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void tokensComeBackAfterWaiting() throws Exception {
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/pan/ABCPE1234F")
                            .header("X-API-Key", "test-key")
                            .header("X-RapidAPI-User", "user-f"))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/pan/ABCPE1234F")
                        .header("X-API-Key", "test-key")
                        .header("X-RapidAPI-User", "user-f"))
                .andExpect(status().isTooManyRequests());

        Thread.sleep(1100);

        mockMvc.perform(get("/pan/ABCPE1234F")
                        .header("X-API-Key", "test-key")
                        .header("X-RapidAPI-User", "user-f"))
                .andExpect(status().isOk());
    }
}
