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
	public boolean addSharedUsers(ObjectId id, ObjectId tripId) {
		Optional<Trip> trip = tripRepository.findById(tripId);
		if (trip.isPresent()) {
			Trip tempTrip = trip.get();
			SharedUser sharedUser = new SharedUser();
			sharedUser.setUserId(id);
			sharedUser.setRole(TripRole.VIEWER);
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
}
