package com.freightfox.weather_info_pincode.service;

import com.freightfox.weather_info_pincode.dto.*;
import com.freightfox.weather_info_pincode.entity.WeatherInfoEntity;
import com.freightfox.weather_info_pincode.repository.WeatherInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherInfoRepository repository;
    private final RestTemplate restTemplate;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    public WeatherResponse getWeather(WeatherRequest request) {

        // 1️ Check DB (CACHE)
        WeatherInfoEntity existing = repository.findByPincode(request.getPincode()).orElse(null);

        if (existing != null) {
            return WeatherResponse.builder()
                    .pincode(existing.getPincode())
                    .city(existing.getCity())
                    .temperature(existing.getTemperature())
                    .humidity(existing.getHumidity())
                    .weatherDescription(existing.getWeatherDescription())
                    .source("CACHE")
                    .build();
        }

        // 2️ Call External Weather API
        String url = apiUrl + "?zip=" + request.getPincode() + ",IN&appid=" + apiKey + "&units=metric";

        WeatherApiResponse apiResponse = restTemplate.getForObject(url, WeatherApiResponse.class);

        // 3️ Save into DB
        WeatherInfoEntity entity = WeatherInfoEntity.builder()
                .pincode(request.getPincode())
                .city(apiResponse.getName())
                .temperature(apiResponse.getMain().getTemp())
                .humidity(apiResponse.getMain().getHumidity())
                .weatherDescription(apiResponse.getWeather().get(0).getDescription())
                .lastFetched(LocalDateTime.now())
                .build();

        repository.save(entity);

        // 4️ Return response
        return WeatherResponse.builder()
                .pincode(entity.getPincode())
                .city(entity.getCity())
                .temperature(entity.getTemperature())
                .humidity(entity.getHumidity())
                .weatherDescription(entity.getWeatherDescription())
                .source("API")
                .build();
    }
}
