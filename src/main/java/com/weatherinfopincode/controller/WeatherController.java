package com.weatherinfopincode.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.weatherinfopincode.dto.WeatherRequest;
import com.weatherinfopincode.dto.WeatherResponse;
import com.weatherinfopincode.service.WeatherService;

import java.time.LocalDate;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    public WeatherResponse getWeather(@Valid WeatherRequest request) {
        return weatherService.getWeather(request);
    }

}

