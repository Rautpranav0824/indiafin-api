package com.pranav.indiafin_api.dto;

public record PanResponse(
        String pan,
        Boolean valid,
        String entityType
) {
}
