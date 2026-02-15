package com.freightfox.weather_info_pincode.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "weather_info",
       uniqueConstraints = @UniqueConstraint(columnNames = {"pincode", "for_date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pincode;

    @Column(name = "for_date", nullable = false)
    private LocalDate forDate;

    private Double temperature;
    private Integer humidity;
    private String weatherDescription;
    
}
