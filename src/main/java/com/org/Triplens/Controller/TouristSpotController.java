package com.org.Triplens.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.Triplens.Services.TouristSpotService;
import com.org.Triplens.entity.TouristSpotCollection;
import com.org.Triplens.repository.TouristSpotRepository;

@RestController
@RequestMapping("/api/spots") // Changed endpoint to avoid conflict with /trips
@CrossOrigin(origins = "http://localhost:5173") // Matched frontend port
public class TouristSpotController {

    @Autowired
    private TouristSpotService service;

    @Autowired
    private TouristSpotRepository repository;

    // 1. GET: Search Only
    @GetMapping("/nearby")
    public List<Map<String, String>> getSpots(@RequestParam String location) {
        return service.getNearbySpots(location);
    }

    // 2. POST: Search AND Save (Caches the search results)
    @PostMapping("/create")
    public TouristSpotCollection createAndSaveItinerary(@RequestParam String location) {
        
        // Step A: Fetch data from the Service (Wikipedia)
        List<Map<String, String>> spots = service.getNearbySpots(location);

        // Step B: Create the Database Object
        TouristSpotCollection collection = new TouristSpotCollection();
        collection.setLocation(location);
        collection.setSpots(spots);

        // Step C: Save to MongoDB
        return repository.save(collection);
    }
    
    // 3. GET: View all cached searches
    @GetMapping("/all")
    public List<TouristSpotCollection> getAllItineraries() {
        return repository.findAll();
    }
}