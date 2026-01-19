package com.triplens.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "trips")
public class Trip {

    @Id
    private ObjectId id;

    private String title;

    private ObjectId ownerUserId;

    private List<SharedUser> sharedUsers;

    private ObjectId itineraryId;

    private Instant createdAt;

    private String status; // ACTIVE, ARCHIVED (future)

    public Trip() {}

    public Trip(String title, ObjectId ownerUserId) {
        this.title = title;
        this.ownerUserId = ownerUserId;
        this.createdAt = Instant.now();
        this.status = "ACTIVE";
    }

    // getters & setters
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ObjectId getOwnerUserId() {
		return ownerUserId;
	}

	public void setOwnerUserId(ObjectId ownerUserId) {
		this.ownerUserId = ownerUserId;
	}

	public List<SharedUser> getSharedUsers() {
		return sharedUsers;
	}

	public void setSharedUsers(List<SharedUser> sharedUsers) {
		this.sharedUsers = sharedUsers;
	}

	public ObjectId getItineraryId() {
		return itineraryId;
	}

	public void setItineraryId(ObjectId itineraryId) {
		this.itineraryId = itineraryId;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

    
}
