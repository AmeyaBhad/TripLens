package com.triplens.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "itineraries")
public class Itinerary {

    @Id
    private ObjectId id;

    private ObjectId tripId;

    private List<Map<String, String>> dayPlans;
    private List<String> hotels;
    private List<String> restaurants;
    private List<String> routes;
    private List<String> festivals;

    public Itinerary() {}

    public Itinerary(ObjectId tripId) {
        this.tripId = tripId;
    }
    
    
    // getters & setters
    

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getTripId() {
		return tripId;
	}

	public void setTripId(ObjectId tripId) {
		this.tripId = tripId;
	}

	public List<Map<String, String>> getDayPlans() {
		return dayPlans;
	}

	public void setDayPlans(List<Map<String, String>> dayPlans) {
		this.dayPlans = dayPlans;
	}

	public List<String> getHotels() {
		return hotels;
	}

	public void setHotels(List<String> hotels) {
		this.hotels = hotels;
	}

	public List<String> getRestaurants() {
		return restaurants;
	}

	public void setRestaurants(List<String> restaurants) {
		this.restaurants = restaurants;
	}

	public List<String> getRoutes() {
		return routes;
	}

	public void setRoutes(List<String> routes) {
		this.routes = routes;
	}

	public List<String> getFestivals() {
		return festivals;
	}

	public void setFestivals(List<String> festivals) {
		this.festivals = festivals;
	}


}
