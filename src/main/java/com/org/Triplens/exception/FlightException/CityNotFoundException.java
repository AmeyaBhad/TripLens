package com.org.Triplens.exception.FlightException;


public class CityNotFoundException extends RuntimeException {

    public CityNotFoundException(String city) {
        super("No airport found for city: " + city);
    }
}

