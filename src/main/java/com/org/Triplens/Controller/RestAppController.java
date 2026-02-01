package com.org.Triplens.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.Triplens.Services.UserService;
import com.org.Triplens.entity.Users;
import com.org.Triplens.exception.NoUserFoundException;
import com.org.Triplens.exception.PasswordIncorrectException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/users")
public class RestAppController {

	@Autowired
	UserService userService;
	@PostMapping("/addUser")
	public boolean addUsers(@RequestParam("name") String name,@RequestParam("email") String email,
							@RequestParam("password") String password) {
		return userService.addUsers(name, password, email);
	}

	@PostMapping("/finduser")
	public Users findUser(@RequestParam("email")String email) {
		try {
			return userService.findUsers(email);
		} catch (NoUserFoundException e) {
			System.out.println("No such User");
			e.printStackTrace();
		}
		return null;
	}
	
	@PostMapping("/authenticate")
	public boolean authenticate(@RequestParam("email")String email,@RequestParam("password")String password) {
		try {
			return userService.authenticate(email, password);
		} catch (NoUserFoundException e) {
			System.out.println("No such User");
			e.printStackTrace();
			return false;
		} catch (PasswordIncorrectException e) {
			System.out.println("Password Incorrect");
			e.printStackTrace();
			return false;
		}
		
	}
}
