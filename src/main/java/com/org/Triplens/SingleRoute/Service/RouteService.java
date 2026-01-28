package com.org.Triplens.SingleRoute.Service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RouteService {

    @Value("${ors.api.key}")
    private String orsApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // 📍 Convert city name → coordinates
    private double[] getCoordinates(String city) {

        String geoUrl =
                "https://api.openrouteservice.org/geocode/search"
                        + "?text=" + city
                        + "&size=1";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", orsApiKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        geoUrl,
                        HttpMethod.GET,
                        entity,
                        String.class
                );

        JSONObject json = new JSONObject(response.getBody());
        JSONArray features = json.getJSONArray("features");

        if (features.isEmpty()) {
            throw new RuntimeException("City not found: " + city);
        }

        JSONArray coords =
                features.getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONArray("coordinates");

        // [lng, lat]
        return new double[]{coords.getDouble(0), coords.getDouble(1)};
    }

    // 🛣 Get route (with alternatives)
    public String getRoute(String from, String to) {

        // ✅ STEP 1: Resolve cities
        double[] start = getCoordinates(from);
        double[] end = getCoordinates(to);

        double startLng = start[0];
        double startLat = start[1];
        double endLng = end[0];
        double endLat = end[1];

        // ✅ STEP 2: ORS directions URL
        String routeUrl =
                "https://api.openrouteservice.org/v2/directions/driving-car"
                        + "?start=" + startLng + "," + startLat
                        + "&end=" + endLng + "," + endLat
                        + "&alternative_routes=true";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", orsApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // ✅ STEP 3: Call ORS
        ResponseEntity<String> response =
                restTemplate.exchange(
                        routeUrl,
                        HttpMethod.GET,
                        entity,
                        String.class
                );

        return response.getBody(); // GeoJSON
    }
}