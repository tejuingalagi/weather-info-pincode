package com.freightfox.weather_info_pincode.service;

import com.freightfox.weather_info_pincode.dto.WeatherRequest;
import com.freightfox.weather_info_pincode.dto.WeatherResponse;

public interface WeatherService {

    WeatherResponse getWeather(WeatherRequest request);
}
