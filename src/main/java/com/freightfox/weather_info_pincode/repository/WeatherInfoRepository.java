package com.freightfox.weather_info_pincode.repository;

import com.freightfox.weather_info_pincode.entity.WeatherInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeatherInfoRepository extends JpaRepository<WeatherInfoEntity, Long> {

    Optional<WeatherInfoEntity> findByPincode(String pincode);

}