package com.weatherinfopincode.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weatherinfopincode.entity.WeatherInfoEntity;

import java.time.LocalDate;
import java.util.Optional;

public interface WeatherInfoRepository
        extends JpaRepository<WeatherInfoEntity, Long> {

    Optional<WeatherInfoEntity> findByPincodeAndForDate(String pincode,
                                                        LocalDate forDate);
}
