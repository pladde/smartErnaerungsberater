package org.salesAPI.util;

import org.springframework.web.client.RestTemplate;

public class ApiWeatherClient {

    private static final String API_URL = "https://api.weather.com/v3/wx/conditions/current";

    /*
    public static WeatherData fetchWeatherData() {
        RestTemplate restTemplate = new RestTemplate();
        WeatherData weatherData = restTemplate.getForObject(API_URL, WeatherData.class);
        return weatherData;
    }

     */
}
