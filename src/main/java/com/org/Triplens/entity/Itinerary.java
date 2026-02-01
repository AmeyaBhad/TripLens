package com.org.Triplens.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@Document(collection = "itineraries")
public class Itinerary {

	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId id;

	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId tripId;

	private List<String> dayPlans;
	private Hotel hotels;
	private Route routes;
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

	public List<String> getDayPlans() {
		return dayPlans;
	}

	public void setDayPlans(List<String> dayPlans) {
		this.dayPlans = dayPlans;
	}

	public Hotel getHotels() {
		return hotels;
	}

	public void setHotels(Hotel hotels) {
		this.hotels = hotels;
	}

	public Route getRoutes() {
		return routes;
	}

	public void setRoutes(Route routes) {
		this.routes = routes;
	}

	public List<FestivalEntity> getFestivals() {
		return festivals;
	}

	public void setFestivals(List<FestivalEntity> festivals) {
		this.festivals = festivals;
	}

	// New Fields for Enhanced Itinerary
	private List<java.util.Map<String, String>> selectedSpots;
	private List<List<Double>> adventureRouteCoordinates;

	public List<java.util.Map<String, String>> getSelectedSpots() {
		return selectedSpots;
	}

	public void setSelectedSpots(List<java.util.Map<String, String>> selectedSpots) {
		this.selectedSpots = selectedSpots;
	}

	public List<List<Double>> getAdventureRouteCoordinates() {
		return adventureRouteCoordinates;
	}

	public void setAdventureRouteCoordinates(List<List<Double>> adventureRouteCoordinates) {
		this.adventureRouteCoordinates = adventureRouteCoordinates;
	}
}
