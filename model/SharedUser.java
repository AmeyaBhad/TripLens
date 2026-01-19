package com.triplens.model;

import org.bson.types.ObjectId;

public class SharedUser {

    private ObjectId userId;
    private TripRole role;

    public SharedUser() {}

    public SharedUser(ObjectId userId, TripRole role) {
        this.userId = userId;
        this.role = role;
    }

    // getters & setters

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
