package com.org.Triplens.DTO;

import java.util.List;

import lombok.Data;

@Data
public class AeroFlightDTO {
    // This inner class handles the root { "departures": [...] }
    @Data
    public static class Wrapper {
        private List<FlightData> departures;
    }

    @Data
    public static class FlightData {
        private String number;
        private String status;
        private Airline airline;
        private Movement movement; // This contains the Destination info
        private Aircraft aircraft;
    }

    @Data
    public static class Movement {
        private Airport airport;
        private ScheduledTime scheduledTime;
        private String terminal;
    }

    @Data
    public static class Airport {
        private String name;
        private String iata;
    }

    @Data
    public static class ScheduledTime {
        private String local; // e.g., "2026-02-10 06:00+05:30"
    }

    @Data
    public static class Airline {
        private String name;
    }
    
    @Data
    public static class Aircraft {
        private String model;
    }
}