package com.freightfox.weather_info_pincode.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Getter
@Setter
public class WeatherRequest {

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "\\d{6}", message = "Pincode must be 6 digits")
    private String pincode;

    private LocalDate forDate;
}
