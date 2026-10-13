package com.pranav.indiafin_api.dto;

import com.pranav.indiafin_api.model.PostOffice;

import java.util.List;

public record PincodeResponse (
    String pincode,
    int count,
    List<PostOffice> postOffices
){
}
