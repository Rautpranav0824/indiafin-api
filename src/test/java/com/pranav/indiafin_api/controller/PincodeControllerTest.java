package com.pranav.indiafin_api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest(properties = "indiafin.api-key=test-key")
@AutoConfigureMockMvc
public class PincodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void knownPincodeReturnsItsPostOffices() throws Exception {
        mockMvc.perform(get("/pincode/504273").header("X-API-Key", "test-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pincode").value("504273"))
                .andExpect(jsonPath("$.count", greaterThan(0)));
    }

    @Test
    void pincodeStartingWithZeroReturns400() throws Exception{
        mockMvc.perform(get("/pincode/012345").header("X-API-Key", "test-key"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid pincode format"));
    }

    @Test
    void tooShortPincodeReturns400() throws Exception{
        mockMvc.perform(get("/pincode/5042").header("X-API-Key", "test-key"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid pincode format"));
    }

    @Test
    void unknownPincodeReturns404() throws Exception{
        mockMvc.perform(get("/pincode/999999").header("X-API-Key", "test-key"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Pincode not found"));
    }
}
