package com.freightfox.weather_info_pincode.service.impl;

import com.freightfox.weather_info_pincode.dto.*;
import com.freightfox.weather_info_pincode.entity.*;
import com.freightfox.weather_info_pincode.repository.*;
import com.freightfox.weather_info_pincode.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final WeatherInfoRepository weatherRepository;
    private final PincodeLocationRepository locationRepository;
    private final RestTemplate restTemplate;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String weatherApiUrl;

    @Value("${weather.geo.url}")
    private String geoApiUrl;

    @Override
    public WeatherResponse getWeather(WeatherRequest request) {

        LocalDate date = (request.getForDate() != null)
                ? request.getForDate()
                : LocalDate.now();

        // 1️ Check weather cache (pincode + date)
        Optional<WeatherInfoEntity> existingWeather =
                weatherRepository.findByPincodeAndForDate(
                        request.getPincode(), date);

        if (existingWeather.isPresent()) {

            WeatherInfoEntity entity = existingWeather.get();

            PincodeLocationEntity location = locationRepository
                    .findById(entity.getPincode())
                    .orElse(null);

            return WeatherResponse.builder()
                    .pincode(entity.getPincode())
                    .city(location != null ? location.getCity() : "UNKNOWN")
                    .temperature(entity.getTemperature())
                    .humidity(entity.getHumidity())
                    .weatherDescription(entity.getWeatherDescription())
                    .source("CACHE")
                    .build();
        }

        // 2️ Get lat/long (check DB first)
        PincodeLocationEntity location =
                locationRepository.findById(request.getPincode())
                        .orElseGet(() -> {

                            String geoUrl = geoApiUrl +
                                    "?zip=" + request.getPincode() +
                                    ",IN&appid=" + apiKey;

                            GeoApiResponse geo =
                                    restTemplate.getForObject(
                                            geoUrl,
                                            GeoApiResponse.class);

                            PincodeLocationEntity newLocation =
                                    PincodeLocationEntity.builder()
                                            .pincode(request.getPincode())
                                            .latitude(geo.getLat())
                                            .longitude(geo.getLon())
                                            .city(geo.getName())
                                            .build();

                            return locationRepository.save(newLocation);
                        });

        // 3️ Call Weather API using lat,long
        String weatherUrl = weatherApiUrl +
                "?lat=" + location.getLatitude() +
                "&lon=" + location.getLongitude() +
                "&appid=" + apiKey +
                "&units=metric";

        WeatherApiResponse apiResponse =
                restTemplate.getForObject(
                        weatherUrl,
                        WeatherApiResponse.class);

        // 4️ Save weather
        WeatherInfoEntity newWeather =
                WeatherInfoEntity.builder()
                        .pincode(request.getPincode())
                        .forDate(date)
                        .temperature(apiResponse.getMain().getTemp())
                        .humidity(apiResponse.getMain().getHumidity())
                        .weatherDescription(
                                apiResponse.getWeather().get(0).getDescription())
                        .build();

        weatherRepository.save(newWeather);

        // 5️ Return response
        return WeatherResponse.builder()
                .pincode(request.getPincode())
                .city(location.getCity())
                .temperature(newWeather.getTemperature())
                .humidity(newWeather.getHumidity())
                .weatherDescription(newWeather.getWeatherDescription())
                .source("API")
                .build();
    }

}
