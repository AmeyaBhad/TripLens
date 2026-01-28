package multi.route.finder.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import multi.route.finder.model.RouteRequest;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class RouteController {

    // ✅ ONLY ADDITION (reads from properties)
    @Value("${ors.api.key}")
    private String orsApiKey;

    @PostMapping("/route")
    public ResponseEntity<String> getRoute(@RequestBody RouteRequest request) {

        String orsUrl =
                "https://api.openrouteservice.org/v2/directions/driving-car/geojson";

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", orsApiKey);

        // Body
        Map<String, Object> body = new HashMap<>();
        body.put("coordinates", request.getCoordinates());

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response =
                restTemplate.postForEntity(orsUrl, entity, String.class);

        return ResponseEntity.ok(response.getBody());
    }
}
