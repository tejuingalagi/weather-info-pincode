package com.weatherinfopincode.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoApiResponse {

    private Double lat;
    private Double lon;
    private String name;

}
