package com.weather.AirQuality;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AirQualityApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirQualityApplication.class, args);
	}

}
