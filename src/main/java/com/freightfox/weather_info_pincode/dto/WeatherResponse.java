package com.freightfox.weather_info_pincode.dto;

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
