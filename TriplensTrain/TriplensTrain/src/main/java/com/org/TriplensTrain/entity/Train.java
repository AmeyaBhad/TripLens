package com.org.TriplensTrain.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "trains")
public class Train {
    @Id
    private String id;
    private String train_no;
    private String train_name;
    private String origin_station;
    private String origin_station_code;
    private String destination_station;
    private String destination_station_code;
    private List<String> intermediate_stations;
    private List<String> classes;
    private String train_type;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTrain_no() {
		return train_no;
	}
	public void setTrain_no(String train_no) {
		this.train_no = train_no;
	}
	public String getTrain_name() {
		return train_name;
	}
	public void setTrain_name(String train_name) {
		this.train_name = train_name;
	}
	public String getOrigin_station() {
		return origin_station;
	}
	public void setOrigin_station(String origin_station) {
		this.origin_station = origin_station;
	}
	public String getOrigin_station_code() {
		return origin_station_code;
	}
	public void setOrigin_station_code(String origin_station_code) {
		this.origin_station_code = origin_station_code;
	}
	public String getDestination_station() {
		return destination_station;
	}
	public void setDestination_station(String destination_station) {
		this.destination_station = destination_station;
	}
	public String getDestination_station_code() {
		return destination_station_code;
	}
	public void setDestination_station_code(String destination_station_code) {
		this.destination_station_code = destination_station_code;
	}
	public List<String> getIntermediate_stations() {
		return intermediate_stations;
	}
	public void setIntermediate_stations(List<String> intermediate_stations) {
		this.intermediate_stations = intermediate_stations;
	}
	public List<String> getClasses() {
		return classes;
	}
	public void setClasses(List<String> classes) {
		this.classes = classes;
	}
	public String getTrain_type() {
		return train_type;
	}
	public void setTrain_type(String train_type) {
		this.train_type = train_type;
	}
    	
    
    
    

}

