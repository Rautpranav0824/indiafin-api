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
public class GstinControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void validGstReturns200() throws Exception {
        mockMvc.perform(get("/gstin/27AAPFU0939F1ZV").header("X-API-Key", "test-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateCode").value("27"))
                .andExpect(jsonPath("$.pan").value("AAPFU0939F"));
    }

    @Test
    void wrongCheckCharacterReturns400() throws Exception {
        mockMvc.perform(get("/gstin/27AAPFU0939F1ZX").header("X-API-Key", "test-key"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid GSTIN checksum"));
    }

    @Test
    void badFormatGstReturns400() throws Exception{
        mockMvc.perform(get("/gstin/hello").header("X-API-Key", "test-key"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid GSTIN format"));
    }

    @Test
    void shortGstReturns400() throws Exception{
        mockMvc.perform(get("/gstin/27AAPFU0939F1Z").header("X-API-Key", "test-key"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid GSTIN format"));

    }
}
