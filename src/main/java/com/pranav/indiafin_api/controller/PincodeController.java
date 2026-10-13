package com.pranav.indiafin_api.controller;

import com.pranav.indiafin_api.dto.PincodeResponse;
import com.pranav.indiafin_api.model.PostOffice;
import com.pranav.indiafin_api.service.PincodeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
public class PincodeController {

    private final PincodeService pincodeService;

    public PincodeController(PincodeService pincodeService) {
        this.pincodeService = pincodeService;
    }

    @GetMapping("/pincode/{code}")
    public PincodeResponse getOffice(@PathVariable String code){
        String pincode = code;

        if(!pincode.matches("^[1-9][0-9]{5}$")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST ,"Invalid pincode format" );
        }

        Optional<List<PostOffice>> result = pincodeService.find(pincode);

        if(result.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND , "Pincode not found");
        }

        List<PostOffice> offices = result.get();
        return new PincodeResponse(pincode, offices.size(), offices);
    }
}
