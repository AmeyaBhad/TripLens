package com.org.Triplens.Services;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.org.Triplens.DAO.ItineraryDao;
import com.org.Triplens.entity.Itinerary;

@Service
public class ItirneraryService {
	@Autowired
	ItineraryDao itirneraryDao;
	
	public void addItinerary(Itinerary itinerary, ObjectId tripId) {
		itirneraryDao.addItinerary(itinerary, tripId);
	}
	
	public Itinerary getItinerary(ObjectId id) {
		return itirneraryDao.getItinerary(id);
	}
	
	public void updateItinerary(ObjectId itineary, Itinerary updatedItinerary) {
		itirneraryDao.updateItinerary(itineary, updatedItinerary);
	}
}
