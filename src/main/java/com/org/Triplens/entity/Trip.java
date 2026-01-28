package com.org.Triplens.entity;

import java.time.Instant;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "trips")
public class Trip {

	 	@Id
	    private ObjectId id;

	    private String title;

	    private ObjectId ownerUserId;

	    private List<SharedUser> sharedUsers;

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
