package com.org.Triplens.Services;

import java.util.ArrayList;

import org.bson.types.ObjectId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.org.Triplens.DAO.TripDao;
import com.org.Triplens.DAO.UsersDao;
import com.org.Triplens.entity.Trip;
import com.org.Triplens.entity.Users;
import com.org.Triplens.exception.NoUserFoundException;
import com.org.Triplens.exception.PasswordIncorrectException;

@Component
public class UserServiceImpl implements UserService {

	@Autowired
	UsersDao userDAO;

	@Autowired
	TripDao tripDao;

	public UserServiceImpl() {

	}

	@Override
	public boolean addUsers(String name, String password, String email) {
		Users user = new Users();
		user.setUserName(name);
		user.setPassword(password);
		user.setEmail(email);
		user.setTripList(new ArrayList<ObjectId>());
		if (userDAO.addUsers(user)) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public Users findUsers(String email) throws NoUserFoundException {
		return userDAO.findUsers(email);
	}

	@Override
	public boolean authenticate(String email, String password) throws NoUserFoundException, PasswordIncorrectException {
		return userDAO.authenticate(email, password);
	}

	@Override
	public boolean addTrip(ObjectId id, Trip trip) {
		ObjectId tripId = tripDao.addTrip(trip);
		return userDAO.addTrip(id, tripId);
	}

}
