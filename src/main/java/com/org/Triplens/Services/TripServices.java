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

	public boolean addSharedUsers(ObjectId tripId, String email) throws NoUserFoundException, NoTripFoundException {
		Users user = userDao.findUsers(email);
		ObjectId id = user.getObjectId();
		if (tripDao.addSharedUsers(id, tripId)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean addTrip(ObjectId userId,TripDTO trip) {
		Trip newTrip = new Trip();
		newTrip.setTitle(trip.getTitle());
		newTrip.setOwnerUserId(userId);
		newTrip.setSharedUsers(new ArrayList<SharedUser>());
		ObjectId tripId=tripDao.addTrip(newTrip);
		userDao.addTrip(userId, tripId);
		return true;
	}
	
	public List<Trip> getTripsByUserId(ObjectId userId) {
	    return tripDao.getTripsByUserId(userId);
	}
	
	public void updateTripStatus(ObjectId tripId, Boolean status) {
		tripDao.updateTripStatus(tripId, status);
	}

}
