package com.org.Triplens.Services;



import org.bson.types.ObjectId;

import com.org.Triplens.entity.Trip;
import com.org.Triplens.entity.Users;
import com.org.Triplens.exception.NoUserFoundException;
import com.org.Triplens.exception.PasswordIncorrectException;

public interface UserService {
	boolean addUsers(String name,String password,String email);
	Users findUsers(String email) throws NoUserFoundException;
	boolean authenticate(String email,String password) throws NoUserFoundException, PasswordIncorrectException;
	boolean addTrip(ObjectId id,Trip trip);
}
