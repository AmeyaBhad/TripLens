package com.org.Triplens.DTO;

import java.util.List;

import lombok.Data;

public class AeroFlightDTO {
	// This inner class handles the root { "departures": [...] }

	public static class Wrapper {
		private List<FlightData> departures;

		public List<FlightData> getDepartures() {
			return departures;
		}

		public void setDepartures(List<FlightData> departures) {
			this.departures = departures;
		}

	}

	public static class FlightData {
		private String number;
		private String status;
		private Airline airline;
		private Movement movement; // This contains the Destination info
		private Aircraft aircraft;

		// Added for Itinerary
		private String price;
		private String arrivalTime;

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public Airline getAirline() {
			return airline;
		}

		public void setAirline(Airline airline) {
			this.airline = airline;
		}

		public Movement getMovement() {
			return movement;
		}

		public void setMovement(Movement movement) {
			this.movement = movement;
		}

		public Aircraft getAircraft() {
			return aircraft;
		}

		public void setAircraft(Aircraft aircraft) {
			this.aircraft = aircraft;
		}

		public String getPrice() {
			return price;
		}

		public void setPrice(String price) {
			this.price = price;
		}

		public String getArrivalTime() {
			return arrivalTime;
		}

		public void setArrivalTime(String arrivalTime) {
			this.arrivalTime = arrivalTime;
		}
	}

	public static class Movement {
		private Airport airport;
		private ScheduledTime scheduledTime;
		private String terminal;

		public Airport getAirport() {
			return airport;
		}

		public void setAirport(Airport airport) {
			this.airport = airport;
		}

		public ScheduledTime getScheduledTime() {
			return scheduledTime;
		}

		public void setScheduledTime(ScheduledTime scheduledTime) {
			this.scheduledTime = scheduledTime;
		}

		public String getTerminal() {
			return terminal;
		}

		public void setTerminal(String terminal) {
			this.terminal = terminal;
		}

	}

	public static class Airport {
		private String name;
		private String iata;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getIata() {
			return iata;
		}

		public void setIata(String iata) {
			this.iata = iata;
		}
	}

	public static class ScheduledTime {
		private String local;
		// e.g., "2026-02-10 06:00+05:30"

		public String getLocal() {
			return local;
		}

		public void setLocal(String local) {
			this.local = local;
		}
	}

	public static class Airline {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public static class Aircraft {
		private String model;

		public String getModel() {
			return model;
		}

		public void setModel(String model) {
			this.model = model;
		}

	}
}