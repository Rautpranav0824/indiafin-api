package com.pranav.indiafin_api.dto;

import java.sql.Time;
import java.time.Instant;
import java.time.LocalTime;

public record PingResponse(String status, String service, String version , Instant timestamp) {

}