package com.pranav.indiafin_api.controller;

import com.pranav.indiafin_api.dto.PanResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class PanController {

    @GetMapping("/pan/{number}")
    public PanResponse getPan (@PathVariable String number){
        String pan = number.toUpperCase();

        if(!pan.matches("^[A-Z]{3}[PCHFATBLJG][A-Z][0-9]{4}[A-Z]$")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid PAN format");
        }
        char type = pan.charAt(3);

        String entityType = switch (type){
            case 'P' -> "Individual";
            case 'C' -> "Company";
            case 'H' -> "Hindu Undivided Family";
            case 'F' -> "Firm";
            case 'A' -> "Association of Persons";
            case 'T' -> "Trust";
            case 'B' -> "Body of Individuals";
            case 'L' -> "Local Authority";
            case 'J' -> "Artificial Juridical Person";
            case 'G' -> "Government";

            default -> "Unknown";
        };

        return new PanResponse(pan , true , entityType);
    }
}
