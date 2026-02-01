package com.org.Triplens.MultiRoute.Model;

import java.util.List;

public class RouteRequest {

    // [[lon,lat], [lon,lat], ...]
    private List<List<Double>> coordinates;

    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }
}