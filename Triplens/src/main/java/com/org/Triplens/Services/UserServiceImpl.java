package com.org.Triplens.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.org.Triplens.DAO.UsersDAO;
import com.org.Triplens.entity.Users;
import com.org.Triplens.exception.NoUserFoundException;
import com.org.Triplens.exception.PasswordIncorrectException;

@Component
public class UserServiceImpl implements UserService {

	@Autowired
	UsersDAO userDAO;

	public UserServiceImpl() {
		
	}

	@Override
	public boolean addUsers(String name, String password, String email) {
		Users user = new Users();
		user.setUserName(name);
		user.setPassword(password);
		user.setEmail(email);
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

}
