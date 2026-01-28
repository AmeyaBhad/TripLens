package com.org.Triplens.MultiRoute.Service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ORSService {

    @Value("${ors.api.key}")
    private String apiKey;

    private static final String ORS_URL =
        "https://api.openrouteservice.org/v2/directions/driving-car/geojson";

    public String getRoute(String requestBody) {

        RestTemplate restTemplate = new RestTemplate();

        org.springframework.http.HttpHeaders headers =
                new org.springframework.http.HttpHeaders();
        headers.set("Authorization", apiKey);
        headers.set("Content-Type", "application/json");

        org.springframework.http.HttpEntity<String> entity =
                new org.springframework.http.HttpEntity<>(requestBody, headers);

        return restTemplate.postForObject(ORS_URL, entity, String.class);
    }
}