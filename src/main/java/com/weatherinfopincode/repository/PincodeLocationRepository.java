package com.weatherinfopincode.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weatherinfopincode.entity.PincodeLocationEntity;

public interface PincodeLocationRepository
        extends JpaRepository<PincodeLocationEntity, String> {
}
