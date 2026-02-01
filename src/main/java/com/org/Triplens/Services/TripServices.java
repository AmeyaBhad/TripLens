package com.org.Triplens.Services;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.org.Triplens.DAO.TripDao;
import com.org.Triplens.DAO.UsersDao;
import com.org.Triplens.DTO.TripDTO;
import com.org.Triplens.entity.SharedUser;
import com.org.Triplens.entity.Trip;
import com.org.Triplens.entity.Users;
import com.org.Triplens.exception.NoTripFoundException;
import com.org.Triplens.exception.NoUserFoundException;

@Component
public class TripServices {

	@Autowired
	UsersDao userDao;

	@Autowired
	TripDao tripDao;

	public boolean addSharedUsers(ObjectId tripId, String email, String role)
			throws NoUserFoundException, NoTripFoundException {
		Users user = userDao.findUsers(email);
		ObjectId id = user.getObjectId();
		if (tripDao.addSharedUsers(id, tripId, role)) {
			return true;
		} else {
			return false;
		}
	}

	public String addTrip(ObjectId userId, TripDTO trip) {
		Trip newTrip = new Trip();
		// Use destination as title if title is missing
		newTrip.setTitle(trip.getDestination() != null ? "Trip to " + trip.getDestination() : "New Trip");
		newTrip.setStartLocation(trip.getStartLocation());
		newTrip.setDestination(trip.getDestination());
		newTrip.setStartDate(trip.getStartDate());
		newTrip.setEndDate(trip.getEndDate());
		newTrip.setTravelers(trip.getTravelers());
		newTrip.setOwnerUserId(userId);
		newTrip.setSharedUsers(new ArrayList<SharedUser>());
		newTrip.setStatus(true); // Default status to true (active)
		ObjectId tripId = tripDao.addTrip(newTrip);
		userDao.addTrip(userId, tripId);
		return tripId.toString();
	}

	public java.util.Map<String, List<Trip>> getTripsByUserId(ObjectId userId) {
		List<Trip> allTrips = tripDao.getTripsByUserId(userId);
		List<Trip> createdTrips = new ArrayList<>();
		List<Trip> sharedTrips = new ArrayList<>();

		for (Trip trip : allTrips) {
			if (trip.getOwnerUserId().equals(userId)) {
				createdTrips.add(trip);
			} else {
				sharedTrips.add(trip);
			}
		}

		java.util.Map<String, List<Trip>> result = new java.util.HashMap<>();
		result.put("created_trips", createdTrips);
		result.put("shared_trips", sharedTrips);

		return result;
	}

	public void updateTripStatus(ObjectId tripId, Boolean status) {
		tripDao.updateTripStatus(tripId, status);
	}

	public void deleteTrip(ObjectId tripId) {
		tripDao.deleteTrip(tripId);
	}

	public Trip getTripById(ObjectId tripId) throws NoTripFoundException {
		return tripDao.findTrip(tripId);
	}

}
