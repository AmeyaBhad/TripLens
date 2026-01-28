package com.org.Triplens.DAO;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.org.Triplens.entity.Itinerary;
import com.org.Triplens.repository.ItineraryRepository;
@Component
public class ItineraryDaoImpl implements ItineraryDao {
	
	@Autowired
	ItineraryRepository itineraryRepository;

	@Override
	public void addItinerary(Itinerary itinerary, ObjectId tripId) {
		itinerary.setTripId(tripId);
		itineraryRepository.save(itinerary);

	}

	@Override
	public Itinerary getItinerary(ObjectId id) {
		Itinerary itinerary = itineraryRepository.findById(id).orElse(null);
		return itinerary;
	}

	@Override
	public void updateItinerary(ObjectId itineary, Itinerary updatedItinerary) {
		updatedItinerary.setId(itineary);
		itineraryRepository.save(updatedItinerary);
	}

}
