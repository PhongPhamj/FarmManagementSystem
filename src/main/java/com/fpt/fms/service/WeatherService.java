package com.fpt.fms.service;

import com.fpt.fms.service.baseservice.IWeatherService;
import com.fpt.fms.web.rest.errors.BaseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class WeatherService implements IWeatherService {

    private final RestTemplate restTemplate;

    @Value("${weather.url}")
    private String url;

    @Value("${weather.url-geo-api}")
    private String geoUrl;

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseEntity<?> getWeatherByCity(String city) {
        Map<String, Object> variable = new HashMap<>();
        variable.put("city", city);

        ResponseEntity<LocationW[]> location = restTemplate.getForEntity(geoUrl + "&q={city}", LocationW[].class,variable);
        if(!location.getStatusCode().is2xxSuccessful()){
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "không có kết nối tới thời tiết hôm nay");
        }
        variable.put("lat", location.getBody()[0].getLat());
        variable.put("lon", location.getBody()[0].getLon());

        ResponseEntity<?> weather = restTemplate.getForEntity(url + "&lat={lat}&lon={lon}", Object.class,variable);
        if(!weather.getStatusCode().is2xxSuccessful()){
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), "không có kết nối tới thời tiết hôm nay");
        }
        return weather;
    }
}
class LocationW{
    private double lat;
    private double lon;

    private String name;

    private String country;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
