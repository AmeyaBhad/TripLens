package com.org.Triplens.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FlightRoute extends Route {
    private String airlineName;
    private String flightNumber;
    private String departureTime;
    private String arrivalTime;
    private String duration;
    private double price;
    private int stops;
    private String origin;
    private String destination;
}
