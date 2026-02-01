package com.org.Triplens.entity;

public class CarRoute extends Route {
	private double distanceKm;
    private double fuelCost;
	public double getDistanceKm() {
		return distanceKm;
	}
	public void setDistanceKm(double distanceKm) {
		this.distanceKm = distanceKm;
	}
	public double getFuelCost() {
		return fuelCost;
	}
	public void setFuelCost(double fuelCost) {
		this.fuelCost = fuelCost;
	}
    
}
