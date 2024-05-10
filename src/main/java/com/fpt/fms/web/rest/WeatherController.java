package com.fpt.fms.web.rest;

import com.fpt.fms.service.baseservice.IWeatherService;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class WeatherController {

    private final IWeatherService weatherService;

    @Autowired
    private Storage storage;

    public WeatherController(IWeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/api/weather/{city}")
    public ResponseEntity<?> weatherByCity(@PathVariable String city) {
        return weatherService.getWeatherByCity(city);
    }
}
