package com.org.Triplens.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "itineraries")
public class Itinerary {

	@Id
	private ObjectId id;

	private ObjectId tripId;

	private List<String> dayPlans;
	private String hotels;
	private String routes;
	private List<FestivalEntity> festivals;

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

	public List< String> getDayPlans() {
		return dayPlans;
	}

	public void setDayPlans(List<String> dayPlans) {
		this.dayPlans = dayPlans;
	}

	public String getHotels() {
		return hotels;
	}

	public void setHotels(String hotels) {
		this.hotels = hotels;
	}

	public String getRoutes() {
		return routes;
	}

	public void setRoutes(String routes) {
		this.routes = routes;
	}

	public List<FestivalEntity> getFestivals() {
		return festivals;
	}

	public void setFestivals(List<FestivalEntity> festivals) {
		this.festivals = festivals;
	}

}
