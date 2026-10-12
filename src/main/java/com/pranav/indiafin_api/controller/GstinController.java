package com.pranav.indiafin_api.controller;

import com.pranav.indiafin_api.dto.GstinResponse;
import com.pranav.indiafin_api.service.GstinChecksum;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class GstinController {

    @GetMapping("/gstin/{number}")
    public GstinResponse getGst(@PathVariable String number){

        String gst = number.toUpperCase();

        if(!gst.matches("^[0-9]{2}[A-Z]{3}[PCHFATBLJG][A-Z][0-9]{4}[A-Z][1-9A-Z]Z[0-9A-Z]$")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST , "Invalid GSTIN format");
        }
        if (!GstinChecksum.isValid(gst)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid GSTIN checksum");
        }

        String stateCode = gst.substring(0,2);
        String pan = gst.substring(2,12);

        return new GstinResponse(gst , true , stateCode , pan);
    }
}
