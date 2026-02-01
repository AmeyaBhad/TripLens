package com.org.Triplens.entity;

import java.time.Instant;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@Document(collection = "trips")
public class Trip {

	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId id;

	private String title;
	private String startLocation;
	private String destination;
	private String startDate;
	private String endDate;
	private Integer travelers;

	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId ownerUserId;

	private List<SharedUser> sharedUsers;

	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId itineraryId;

	private Instant createdAt;

	private Boolean status;

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

	public String getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(String startLocation) {
		this.startLocation = startLocation;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Integer getTravelers() {
		return travelers;
	}

	public void setTravelers(Integer travelers) {
		this.travelers = travelers;
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

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}
}
