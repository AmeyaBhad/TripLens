package com.org.Triplens.Controller;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.Triplens.DTO.TripDTO;
import com.org.Triplens.Services.TripServices;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/trips")
public class TripController {
	@Autowired
	TripServices tripService;

	@PostMapping("/addTrip")
	public boolean addTrip(@RequestParam("userId") String userId, @RequestBody TripDTO trip) {
		ObjectId userObjectId = new ObjectId(userId);
		tripService.addTrip(userObjectId, trip);
		return true; 
	}
	
	@PostMapping("/getTripsByUserId")
	public Object getTripsByUserId(@RequestParam("userId") String userId) {
		ObjectId userObjectId = new ObjectId(userId);
		return tripService.getTripsByUserId(userObjectId);
	}
	
	@PostMapping("/updateTripStatus")
	public void updateTripStatus(@RequestParam("tripId") String tripId, @RequestParam("status") Boolean status) {
		ObjectId tripObjectId = new ObjectId(tripId);
		tripService.updateTripStatus(tripObjectId, status);
	}
	
	@PostMapping("/addSharedUser")
	public boolean addSharedUsers(@RequestParam("tripId") String tripId, @RequestParam("email") String email) {
		ObjectId tripObjectId = new ObjectId(tripId);
		try {
			return tripService.addSharedUsers(tripObjectId, email);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
