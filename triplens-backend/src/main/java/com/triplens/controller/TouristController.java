package com.triplens.controller;

import com.triplens.model.Itinerary;
import com.triplens.repository.ItineraryRepository;
import com.triplens.service.TouristSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trip")
public class TouristController {

    @Autowired
    private TouristSpotService service;

    @Autowired
    private ItineraryRepository repository;

    // 1. GET: Search Only (Good for previewing data without saving)
    // URL: http://localhost:8088/api/trip/nearby?location=Pune
    @GetMapping("/nearby")
    public List<Map<String, String>> getSpots(@RequestParam String location) {
        return service.getNearbySpots(location);
    }

    // 2. POST: Search AND Save (This stores the data in MongoDB)
    // URL: http://localhost:8088/api/trip/create?location=Pune
    @PostMapping("/create")
    public Itinerary createAndSaveItinerary(@RequestParam String location) {
        
        // Step A: Fetch data from the Service (Wikipedia)
        List<Map<String, String>> spots = service.getNearbySpots(location);

        // Step B: Create the Database Object
        Itinerary itinerary = new Itinerary();
        itinerary.setLocation(location);
        itinerary.setSpots(spots);

        // Step C: Save to MongoDB and return the saved object
        return repository.save(itinerary);
    }
    
    // 3. GET: View all saved trips
    // URL: http://localhost:8088/api/trip/all
    @GetMapping("/all")
    public List<Itinerary> getAllItineraries() {
        return repository.findAll();
    }
}