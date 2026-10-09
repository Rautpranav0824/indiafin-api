package com.pranav.indiafin_api.model;

public record Branch(
        String ifsc,
        String bank,
        String branch,
        String address,
        String city,
        String district,
        String state,
        String micr,
        String contact,
        boolean neft,
        boolean rtgs,
        boolean imps,
        boolean upi
    ){

}
