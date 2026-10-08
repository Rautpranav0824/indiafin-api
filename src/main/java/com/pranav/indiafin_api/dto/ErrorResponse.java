package com.pranav.indiafin_api.dto;
import java.time.Instant;


public record ErrorResponse(int status , String error , String message , Instant timestamp) {

}