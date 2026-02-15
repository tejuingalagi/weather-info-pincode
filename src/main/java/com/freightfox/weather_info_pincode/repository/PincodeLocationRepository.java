package com.freightfox.weather_info_pincode.repository;

import com.freightfox.weather_info_pincode.entity.PincodeLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PincodeLocationRepository
        extends JpaRepository<PincodeLocationEntity, String> {
}
