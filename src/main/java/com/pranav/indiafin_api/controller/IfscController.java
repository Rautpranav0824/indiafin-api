package com.pranav.indiafin_api.controller;

import com.pranav.indiafin_api.dto.IfscResponse;
import com.pranav.indiafin_api.model.Branch;
import com.pranav.indiafin_api.service.IfscService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
public class IfscController {

    private final IfscService ifscService;

    public IfscController(IfscService ifscService) {
        this.ifscService = ifscService;
    }


    @GetMapping("/ifsc/{code}")
    public IfscResponse getIfsc(@PathVariable String code){
        String ifsc = code.toUpperCase();

        if(!ifsc.matches("^[A-Z]{4}0[A-Z0-9]{6}$")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST , "Invalid IFSC Code Format");
        }

        Optional<Branch> result = ifscService.find(ifsc);

        if(result.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND , "IFSC code not found");
        }

        Branch b = result.get();
        return new IfscResponse(b.ifsc(), b.bank(), b.branch(), b.address(), b.city(),
                b.district(), b.state(), b.micr(), b.contact(),
                b.neft(), b.rtgs(), b.imps(), b.upi());
    }
}
