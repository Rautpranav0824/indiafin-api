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
class IfscControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void knownIfscReturnsBranchDetails() throws Exception {
        mockMvc.perform(get("/ifsc/SBIN0000001").header("X-API-Key", "test-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bank").value("State Bank of India"))
                .andExpect(jsonPath("$.branch").value("KOLKATA MAIN"));
    }

    @Test
    void malformedIfscReturns400() throws Exception {
        mockMvc.perform(get("/ifsc/hello").header("X-API-Key", "test-key"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid IFSC Code Format"));
    }

    @Test
    void wellFormedButUnknownIfscReturns404() throws Exception {
        mockMvc.perform(get("/ifsc/ABCD0123456").header("X-API-Key", "test-key"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("IFSC code not found"));
    }

    @Test
    void correctFormatButLowerCaseReturns200() throws Exception {
        mockMvc.perform(get("/ifsc/sbin0000001").header("X-API-Key", "test-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bank").value("State Bank of India"))
                .andExpect(jsonPath("$.branch").value("KOLKATA MAIN"))
                .andExpect(jsonPath("$.ifsc").value("SBIN0000001"));

    }

    @Test
    void fifthCharNotZeroReturns400() throws Exception {
        mockMvc.perform(get("/ifsc/SBIN1001234").header("X-API-Key", "test-key"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid IFSC Code Format"));
    }
}
