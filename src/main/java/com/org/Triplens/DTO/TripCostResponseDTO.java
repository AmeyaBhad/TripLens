package com.org.Triplens.DTO;


import lombok.Data;
import java.util.Map;

@Data
public class TripCostResponseDTO {
    private double distanceKm;
    private Map<String, Double> pricing;
	public double getDistanceKm() {
		return distanceKm;
	}
	public void setDistanceKm(double distanceKm) {
		this.distanceKm = distanceKm;
	}
	public Map<String, Double> getPricing() {
		return pricing;
	}
	public void setPricing(Map<String, Double> pricing) {
		this.pricing = pricing;
	}
}
