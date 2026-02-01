package com.org.Triplens.entity;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Users")
public class Users {
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	ObjectId objectId;
	String userName;
	String password;
	String email;
	List<ObjectId> tripList;

	public List<ObjectId> getTripList() {
		return tripList;
	}

	public void setTripList(List<ObjectId> tripList) {
		this.tripList = tripList;
	}

	public ObjectId getObjectId() {
		return objectId;
	}

	public void setObjectId(ObjectId objectId) {
		this.objectId = objectId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Users [objectId=" + objectId + ", userName=" + userName + ", password=" + password + ", email=" + email
				+ "]";
	}
}
