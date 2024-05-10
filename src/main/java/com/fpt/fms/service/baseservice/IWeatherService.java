package com.fpt.fms.service.baseservice;

import org.springframework.http.ResponseEntity;

public interface IWeatherService {

    ResponseEntity<?> getWeatherByCity(String city);

}
