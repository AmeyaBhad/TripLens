package com.org.Triplens.entity;

import java.util.List;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Formerly "Itinerary" in your module.
 * Renamed to distinct it from the main User Trip Itinerary.
 * This stores the "Gold List" of spots for a specific city.
 */
@Document(collection = "tourist_spot_collections")
public class TouristSpotCollection {

    @Id
    private String id; 

    private String location;
    private List<Map<String, String>> spots;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public List<Map<String, String>> getSpots() { return spots; }
    public void setSpots(List<Map<String, String>> spots) { this.spots = spots; }
}