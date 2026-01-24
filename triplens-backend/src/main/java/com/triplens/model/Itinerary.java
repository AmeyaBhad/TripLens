package com.triplens.model;

import java.util.List;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "itineraries")
public class Itinerary {

    @Id
    private String id; // Changed from ObjectId to String for cleaner JSON

    private String location;
    private List<Map<String, String>> spots;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public List<Map<String, String>> getSpots() { return spots; }
    public void setSpots(List<Map<String, String>> spots) { this.spots = spots; }
}