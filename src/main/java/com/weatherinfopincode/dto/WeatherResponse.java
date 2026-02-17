package com.weatherinfopincode.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeatherResponse {

    private String pincode;
    private String city;   
    private Double temperature;
    private Integer humidity;
    private String weatherDescription;
    private String source;
}
