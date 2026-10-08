package com.pranav.indiafin_api.controller;

import com.pranav.indiafin_api.dto.PingResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalTime;

@RestController
public class PingController {

    @GetMapping("/ping")
    public PingResponse ping(){
        Instant currentTime = Instant.now();
        return new PingResponse("UP" , "IndiaFin" , "1.0.0" , currentTime);
    }
}
