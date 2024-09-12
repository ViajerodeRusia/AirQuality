package com.weather.AirQuality.http;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "air-quality",
        url = "https://api.waqi.info/feed/here/?token=883f293d7db9ec696e74acc19d5781afef071c99")
public interface AirQualityInterface {
    @GetMapping
    ResponseEntity<String> getAirQualityData();
}
