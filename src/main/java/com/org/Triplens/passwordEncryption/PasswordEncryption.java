package com.org.Triplens.passwordEncryption;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class PasswordEncryption {
	
	
	final BCryptPasswordEncoder bcyrBCryptPasswordEncoder=new BCryptPasswordEncoder();

	public String PasswordEncrption(String password) {
		return bcyrBCryptPasswordEncoder.encode(password);
	}

	public boolean matches(String rawPassword, String encodedPassword) {
        return bcyrBCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }

}
