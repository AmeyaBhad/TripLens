package com.org.Triplens.DAO;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.org.Triplens.entity.Itinerary;
import com.org.Triplens.entity.Trip;
import com.org.Triplens.repository.ItineraryRepository;
import com.org.Triplens.repository.TripRepository;
import java.util.Optional;

@Component
public class ItineraryDaoImpl implements ItineraryDao {

	@Autowired
	ItineraryRepository itineraryRepository;

	@Autowired
	TripRepository tripRepository;

	@Override
	public void addItinerary(Itinerary itinerary, ObjectId tripId) {
		itinerary.setTripId(tripId);
		Itinerary savedItinerary = itineraryRepository.save(itinerary);

		Optional<Trip> tripOpt = tripRepository.findById(tripId);
		if (tripOpt.isPresent()) {
			Trip trip = tripOpt.get();
			trip.setItineraryId(savedItinerary.getId());
			tripRepository.save(trip);
		}
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
