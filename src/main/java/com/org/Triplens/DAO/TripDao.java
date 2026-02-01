package com.org.Triplens.DAO;

import java.util.List;

import org.bson.types.ObjectId;

import com.org.Triplens.entity.Trip;
import com.org.Triplens.exception.NoTripFoundException;

public interface TripDao {

	ObjectId addTrip(Trip trip);

	Trip findTrip(ObjectId id) throws NoTripFoundException;

	boolean addSharedUsers(ObjectId id, ObjectId tripId, String role);

	public List<Trip> getTripsByUserId(ObjectId userId);

	public void updateTripStatus(ObjectId tripId, Boolean status);

	void deleteTrip(ObjectId tripId);
}
