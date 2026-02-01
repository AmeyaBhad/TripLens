package com.org.Triplens.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Train")
public class Train extends Route {
	@Id
	private String id;

	@org.springframework.data.mongodb.core.mapping.Field("train_no")
	private String trainNumber;

	@org.springframework.data.mongodb.core.mapping.Field("train_name")
	private String trainName;

	@org.springframework.data.mongodb.core.mapping.Field("origin_station")
	private String originStation;

	@org.springframework.data.mongodb.core.mapping.Field("origin_station_code")
	private String originStationCode;

	@org.springframework.data.mongodb.core.mapping.Field("destination_station")
	private String destinationStation;

	@org.springframework.data.mongodb.core.mapping.Field("destination_station_code")
	private String destinationStationCode;

	@org.springframework.data.mongodb.core.mapping.Field("intermediate_stations")
	private List<String> intermediateStations;

	@org.springframework.data.mongodb.core.mapping.Field("classes")
	private List<String> classes;

	@org.springframework.data.mongodb.core.mapping.Field("train_type")
	private String trainType;

	// New fields for Itinerary - Not in DB, populated in Service
	private String departureTime;

	private String arrivalTime;

	private String duration;

	private String origin;

	private String destination;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTrainNumber() {
		return trainNumber;
	}

	public void setTrainNumber(String trainNumber) {
		this.trainNumber = trainNumber;
	}

	public String getTrainName() {
		return trainName;
	}

	public void setTrainName(String trainName) {
		this.trainName = trainName;
	}

	public String getOriginStation() {
		return originStation;
	}

	public void setOriginStation(String originStation) {
		this.originStation = originStation;
	}

	public String getOriginStationCode() {
		return originStationCode;
	}

	public void setOriginStationCode(String originStationCode) {
		this.originStationCode = originStationCode;
	}

	public String getDestinationStation() {
		return destinationStation;
	}

	public void setDestinationStation(String destinationStation) {
		this.destinationStation = destinationStation;
	}

	public String getDestinationStationCode() {
		return destinationStationCode;
	}

	public void setDestinationStationCode(String destinationStationCode) {
		this.destinationStationCode = destinationStationCode;
	}

	public List<String> getIntermediateStations() {
		return intermediateStations;
	}

	public void setIntermediateStations(List<String> intermediateStations) {
		this.intermediateStations = intermediateStations;
	}

	public List<String> getClasses() {
		return classes;
	}

	public void setClasses(List<String> classes) {
		this.classes = classes;
	}

	public String getTrainType() {
		return trainType;
	}

	public void setTrainType(String trainType) {
		this.trainType = trainType;
	}

	public String getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	public String getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}
}
