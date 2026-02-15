package com.freightfox.weather_info_pincode.controller;

import com.freightfox.weather_info_pincode.dto.WeatherRequest;
import com.freightfox.weather_info_pincode.dto.WeatherResponse;
import com.freightfox.weather_info_pincode.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @PostMapping
    public WeatherResponse getWeather(@Valid @RequestBody WeatherRequest request) {
        return weatherService.getWeather(request);
    }
}
