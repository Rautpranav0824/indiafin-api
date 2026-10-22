package com.pranav.indiafin_api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "indiafin.api-key=test-key")
@AutoConfigureMockMvc
public class ApiKeyFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void missingKeyIsRejected() throws Exception {
        mockMvc.perform(get("/ifsc/SBIN0000001"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Missing or invalid API key"));
    }

    @Test
    void wrongKeyIsRejected() throws Exception {
        mockMvc.perform(get("/ifsc/SBIN0000001").header("X-API-Key", "wrong"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Missing or invalid API key"));
    }

    @Test
    void correctKeyIsAccepted() throws Exception{
        mockMvc.perform(get("/pan/ABCPE1234F").header("X-API-Key", "test-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pan").value("ABCPE1234F"));

    }

    @Test
    void pingNeedsNoKey() throws Exception {
        mockMvc.perform(get("/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
