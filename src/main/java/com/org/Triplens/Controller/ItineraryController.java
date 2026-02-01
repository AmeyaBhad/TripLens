package com.org.Triplens.Controller;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.Triplens.Services.ItirneraryService;
import com.org.Triplens.entity.Itinerary;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/itineraries")
public class ItineraryController {
	@Autowired
	ItirneraryService itirneraryService;
	
	@PostMapping("/addItinerary")
	public void addItinerary(@RequestParam("tripId") String tripId, @RequestBody Itinerary itinerary) {
		ObjectId tripObjectId = new ObjectId(tripId);
		itirneraryService.addItinerary(itinerary, tripObjectId);
	}
	
	@PostMapping("/getItinerary")
	public Itinerary getItinerary(@RequestParam("itineraryId") String itineraryId) {
		ObjectId itineraryObjectId = new ObjectId(itineraryId);
		return itirneraryService.getItinerary(itineraryObjectId);
	}
	
	@PostMapping("/updateItinerary")
	public void updateItinerary(@RequestParam("itineraryId") String itineraryId, @RequestBody Itinerary updatedItinerary) {
		ObjectId itineraryObjectId = new ObjectId(itineraryId);
		itirneraryService.updateItinerary(itineraryObjectId, updatedItinerary);
	}
}
