package com.org.Triplens.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "Airport")
public class Airport {

    @Id
    private String id;

    private String cityName;
    private String airportName;
    private String iataCode;
    private String state;
    private String country;

    // getters & setters
}
