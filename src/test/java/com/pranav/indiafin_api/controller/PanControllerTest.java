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
public class PanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void validPanReturnsIndividual() throws Exception {
        mockMvc.perform(get("/pan/ABCPE1234F").header("X-API-Key", "test-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entityType").value("Individual"));
    }

    @Test
    void invalidPanReturns400() throws Exception {
        mockMvc.perform(get("/pan/hello").header("X-API-Key", "test-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Invalid PAN format"));
    }
}
