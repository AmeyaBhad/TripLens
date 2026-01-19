package com.org.Triplens.DAO;

import com.org.Triplens.entity.Users;
import com.org.Triplens.exception.NoUserFoundException;
import com.org.Triplens.exception.PasswordIncorrectException;

public interface UsersDAO {
	boolean addUsers(Users users);
	Users findUsers(String email) throws NoUserFoundException;
	boolean authenticate(String email,String password) throws NoUserFoundException, PasswordIncorrectException;
}
