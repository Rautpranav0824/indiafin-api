package com.pranav.indiafin_api.model;

public record PostOffice(
        String officeName,
        String pincode ,
        String officeType ,
        boolean delivery ,
        String district,
        String state,
        Double latitude,
        Double longitude
) {
}

// officeName, pincode, officeType, delivery, district, state, latitude, longitude