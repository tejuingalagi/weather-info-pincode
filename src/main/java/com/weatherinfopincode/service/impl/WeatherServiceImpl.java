package com.weatherinfopincode.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.weatherinfopincode.dto.*;
import com.weatherinfopincode.entity.*;
import com.weatherinfopincode.exception.InvalidDateException;
import com.weatherinfopincode.exception.PincodeNotFoundException;
import com.weatherinfopincode.repository.*;
import com.weatherinfopincode.service.WeatherService;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
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

    @Value("${weather.default.country:IN}")
    private String country;

    @Override
    public WeatherResponse getWeather(WeatherRequest request) {

        LocalDate date = request.getForDate() != null ? request.getForDate() : LocalDate.now();

        // API limitation: only current date supported
        if (!date.equals(LocalDate.now())) {
            throw new InvalidDateException("Only current date weather supported");
        }

        // 1️ Check cache
        Optional<WeatherInfoEntity> cached =
                weatherRepository.findByPincodeAndForDate(request.getPincode(), date);

        if (cached.isPresent()) {
            WeatherInfoEntity entity = cached.get();

            PincodeLocationEntity location =
                    locationRepository.findById(entity.getPincode()).orElse(null);

            return WeatherResponse.builder()
                    .pincode(entity.getPincode())
                    .city(location != null ? location.getCity() : "UNKNOWN")
                    .temperature(entity.getTemperature())
                    .humidity(entity.getHumidity())
                    .weatherDescription(entity.getWeatherDescription())
                    .source("CACHE")
                    .build();
        }

        // 2️ Get lat/long (from DB or API)
        PincodeLocationEntity location = locationRepository.findById(request.getPincode())
                .orElseGet(() -> fetchAndSaveLocation(request.getPincode()));

        // 3️ Fetch weather from API
        WeatherApiResponse apiResponse = fetchWeather(location);

        if (apiResponse == null || apiResponse.getMain() == null) {
            throw new RuntimeException("Weather service unavailable");
        }

        // 4️ Save weather
        WeatherInfoEntity newWeather = WeatherInfoEntity.builder()
                .pincode(request.getPincode())
                .forDate(date)
                .temperature(apiResponse.getMain().getTemp())
                .humidity(apiResponse.getMain().getHumidity())
                .weatherDescription(
                        apiResponse.getWeather() != null && !apiResponse.getWeather().isEmpty()
                                ? apiResponse.getWeather().get(0).getDescription()
                                : "No description")
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

    // Fetch location from Geo API and store
    private PincodeLocationEntity fetchAndSaveLocation(String pincode) {

        String geoUrl = geoApiUrl + "?zip=" + pincode + "," + country + "&appid=" + apiKey;

        GeoApiResponse geo = restTemplate.getForObject(geoUrl, GeoApiResponse.class);

        if (geo == null || geo.getLat() == null) {
            throw new PincodeNotFoundException("Invalid pincode: " + pincode);
        }

        return locationRepository.save(
                PincodeLocationEntity.builder()
                        .pincode(pincode)
                        .latitude(geo.getLat())
                        .longitude(geo.getLon())
                        .city(geo.getName())
                        .build());
    }

    // Call OpenWeather API
    private WeatherApiResponse fetchWeather(PincodeLocationEntity location) {

        String url = weatherApiUrl +
                "?lat=" + location.getLatitude() +
                "&lon=" + location.getLongitude() +
                "&appid=" + apiKey +
                "&units=metric";

        return restTemplate.getForObject(url, WeatherApiResponse.class);
    }
}



