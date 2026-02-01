package com.org.Triplens.DAO;

import org.bson.types.ObjectId;

import com.org.Triplens.entity.Itinerary;

public interface ItineraryDao {
	void addItinerary(Itinerary itinerary, ObjectId tripId);
	Itinerary getItinerary(ObjectId id);
	void updateItinerary(ObjectId itineary, Itinerary updatedItinerary);
}
