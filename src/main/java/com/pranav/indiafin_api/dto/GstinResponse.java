package com.pranav.indiafin_api.dto;

public record GstinResponse(
        String gstin ,
        Boolean valid ,
        String stateCode ,
        String pan
) {
}
