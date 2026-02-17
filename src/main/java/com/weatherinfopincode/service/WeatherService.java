package com.weatherinfopincode.service;

import com.weatherinfopincode.dto.WeatherRequest;
import com.weatherinfopincode.dto.WeatherResponse;

public interface WeatherService {

    WeatherResponse getWeather(WeatherRequest request);
}
