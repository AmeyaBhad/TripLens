package com.routefinder.routefinder.controller;

import com.routefinder.routefinder.service.RouteService;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/route")
    public ResponseEntity<?> getRoute(
            @RequestParam String from,
            @RequestParam String to
    ) {
        try {
            return ResponseEntity.ok(routeService.getRoute(from, to).toString());
        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error.toString());
        }
    }
}
