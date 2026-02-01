package com.org.Triplens.DAO;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.org.Triplens.entity.SharedUser;
import com.org.Triplens.entity.Trip;
import com.org.Triplens.entity.TripRole;
import com.org.Triplens.exception.NoTripFoundException;
import com.org.Triplens.repository.TripRepository;

@Component
public class TripDaoImpl implements TripDao {

	@Autowired
	TripRepository tripRepository;

	@Override
	public ObjectId addTrip(Trip trip) {
		ObjectId id = null;
		id = tripRepository.save(trip).getId();

		return id;

	}

	@Override
	public Trip findTrip(ObjectId id) throws NoTripFoundException {
		Optional<Trip> trip = tripRepository.findById(id);
		if (trip.isPresent()) {
			return trip.get();
		}
		throw new NoTripFoundException();
	}

	@Override
	public boolean addSharedUsers(ObjectId id, ObjectId tripId, String role) {
		Optional<Trip> trip = tripRepository.findById(tripId);
		if (trip.isPresent()) {
			Trip tempTrip = trip.get();
			SharedUser sharedUser = new SharedUser();
			sharedUser.setUserId(id);
			try {
				sharedUser.setRole(TripRole.valueOf(role.toUpperCase()));
			} catch (IllegalArgumentException | NullPointerException e) {
				sharedUser.setRole(TripRole.VIEWER); // Default to VIEWER if invalid
			}
			tempTrip.getSharedUsers().add(sharedUser);
			tripRepository.save(tempTrip);
			return true;
		}
		return false;
	}

	public List<Trip> getTripsByUserId(ObjectId userId) {
		return tripRepository.findByOwnerUserIdOrSharedUsersUserId(userId, userId);
	}

	@Override
	public void updateTripStatus(ObjectId tripId, Boolean status) {
		Optional<Trip> trip = tripRepository.findById(tripId);
		if (trip.isPresent()) {
			Trip tempTrip = trip.get();
			tempTrip.setStatus(status);
			tripRepository.save(tempTrip);
		}
	}

	@Autowired
	com.org.Triplens.repository.ItineraryRepository itineraryRepository;

	@Autowired
	UsersDao usersDao;

	@Override
	public void deleteTrip(ObjectId tripId) {
		Optional<Trip> tripOpt = tripRepository.findById(tripId);
		if (tripOpt.isPresent()) {
			Trip trip = tripOpt.get();
			// Delete Itinerary
			if (trip.getItineraryId() != null) {
				itineraryRepository.deleteById(trip.getItineraryId());
			}
			// Remove from User's trip list
			if (trip.getOwnerUserId() != null) {
				usersDao.removeTrip(trip.getOwnerUserId(), tripId);
			}
		}
		tripRepository.deleteById(tripId);
	}
}
