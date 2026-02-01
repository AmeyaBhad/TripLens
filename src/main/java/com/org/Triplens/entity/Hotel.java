package com.org.Triplens.entity;

import org.bson.types.ObjectId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "hotels")
public class Hotel {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    private String name;
    private String city;
    private String address;
    private double pricePerNight;
    private int rating; // 1 to 5 stars
    private Object wifiAvailable;
    private Object parkingAvailable;

    // Constructors
    public Hotel() {
    }

    public Hotel(String name, String city, String address, double pricePerNight, int rating) {
        this.name = name;
        this.city = city;
        this.address = address;
        this.pricePerNight = pricePerNight;
        this.rating = rating;
    }

    // Getters and Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public boolean isWifiAvailable() {
        if (wifiAvailable instanceof Boolean) {
            return (Boolean) wifiAvailable;
        } else if (wifiAvailable instanceof Number) {
            return ((Number) wifiAvailable).intValue() > 0;
        }
        return false;
    }

    public void setWifiAvailable(Object wifiAvailable) {
        this.wifiAvailable = wifiAvailable;
    }

    public boolean isParkingAvailable() {
        if (parkingAvailable instanceof Boolean) {
            return (Boolean) parkingAvailable;
        } else if (parkingAvailable instanceof Number) {
            return ((Number) parkingAvailable).intValue() > 0;
        }
        return false;
    }

    public void setParkingAvailable(Object parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
    }
}
