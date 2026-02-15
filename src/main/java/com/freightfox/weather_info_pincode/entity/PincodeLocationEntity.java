package com.freightfox.weather_info_pincode.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "pincode_location")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PincodeLocationEntity {

    @Id
    @Column(nullable = false, unique = true)
    private String pincode;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private String city;
}

