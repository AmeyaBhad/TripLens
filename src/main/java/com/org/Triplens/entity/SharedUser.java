package com.org.Triplens.entity;

import org.bson.types.ObjectId;

public class SharedUser {

	private ObjectId userId;
    private TripRole role;
	public ObjectId getUserId() {
		return userId;
	}
	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}
	public TripRole getRole() {
		return role;
	}
	public void setRole(TripRole role) {
		this.role = role;
	}

}
