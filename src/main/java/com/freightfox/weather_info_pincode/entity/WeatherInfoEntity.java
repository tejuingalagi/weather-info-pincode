package com.freightfox.weather_info_pincode.entity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_info", uniqueConstraints = @UniqueConstraint(columnNames = "pincode"))
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

	    private String city;

	    private Double temperature;

	    private Integer humidity;

	    private String weatherDescription;

	    private LocalDateTime lastFetched;

}
