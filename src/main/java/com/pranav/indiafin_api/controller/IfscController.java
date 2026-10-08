package com.pranav.indiafin_api.controller;

import com.pranav.indiafin_api.dto.IfscResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class IfscController {

    @GetMapping("/ifsc/{code}")
    public IfscResponse getIfsc(@PathVariable String code){
        String ifsc = code.toUpperCase();

        if(!ifsc.matches("^[A-Z]{4}0[A-Z0-9]{6}$")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST , "Invalid IFSC Code Format");
        }

        return new IfscResponse(ifsc , "Demo Bank" , "Demo Branch");
    }
}
