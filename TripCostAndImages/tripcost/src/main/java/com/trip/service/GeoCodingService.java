package com.trip.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class GeoCodingService {

    private final RestTemplate restTemplate = new RestTemplate();

    public double[] getCoordinates(String place) {
        String url =
          "https://nominatim.openstreetmap.org/search?q="
          + place + "&format=json";

        List<Map<String, Object>> response =
          restTemplate.getForObject(url, List.class);

        if (response == null || response.isEmpty())
            throw new RuntimeException("Location not found");

        double lat = Double.parseDouble(response.get(0).get("lat").toString());
        double lon = Double.parseDouble(response.get(0).get("lon").toString());

        return new double[]{lat, lon};
    }
}

