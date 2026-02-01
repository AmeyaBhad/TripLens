package com.org.Triplens.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
		@JsonSubTypes.Type(value = CarRoute.class, name = "CAR"),
		@JsonSubTypes.Type(value = Train.class, name = "TRAIN"),
		@JsonSubTypes.Type(value = FlightRoute.class, name = "FLIGHT")
})
public class Route {
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
