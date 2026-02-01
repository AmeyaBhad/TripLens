package com.org.Triplens.DAO;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.org.Triplens.entity.Users;
import com.org.Triplens.exception.NoUserFoundException;
import com.org.Triplens.exception.PasswordIncorrectException;
import com.org.Triplens.passwordEncryption.PasswordEncryption;
import com.org.Triplens.repository.UsersRepository;

@Service
public class UsersDAOImpl implements UsersDao {
	@Autowired
	UsersRepository userRepository;

	@Autowired
	PasswordEncryption passwordEncryption;

	@Override
	public boolean addUsers(Users users) {
		users.setPassword(passwordEncryption.PasswordEncrption(users.getPassword()));
		if (userRepository.save(users) != null) {
			return true;
		}
		return false;
	}

	@Override
	public Users findUsers(String email) throws NoUserFoundException {

		Optional<Users> user = userRepository.findByEmail(email);
		try {
			if (user.isPresent()) {
				return user.get();
			} else {
				throw new NoUserFoundException();
			}
		} finally {

		}

	}

	@Override
	public boolean authenticate(String email, String password)
			throws NoUserFoundException, PasswordIncorrectException {

		Optional<Users> tempuser = userRepository.findByEmail(email);

		if (tempuser.isEmpty()) {
			throw new NoUserFoundException();
		}

		Users entityUser = tempuser.get();

		boolean isMatch = passwordEncryption.matches(
				password, // raw password from request
				entityUser.getPassword() // encrypted password from DB
		);

		if (isMatch) {
			return true;
		} else {
			throw new PasswordIncorrectException();
		}
	}

	@Override
	public boolean addTrip(ObjectId id, ObjectId tripId) {
		Users user = userRepository.findById(id).orElseThrow();
		user.getTripList().add(tripId);
		if (userRepository.save(user) != null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean removeTrip(ObjectId userId, ObjectId tripId) {
		Optional<Users> userOpt = userRepository.findById(userId);
		if (userOpt.isPresent()) {
			Users user = userOpt.get();
			if (user.getTripList() != null) {
				user.getTripList().remove(tripId);
				userRepository.save(user);
				return true;
			}
		}
		return false;
	}

}
