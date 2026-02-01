package com.org.Triplens.Services.Flight;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.org.Triplens.DTO.AeroFlightDTO;

import reactor.core.publisher.Mono;

@Service
public class FlightService {

	// @Autowired
	// private WebClient webClient;

	private final WebClient webClient;

	public FlightService(WebClient webClient) {
		this.webClient = webClient;
	}

	public Mono<List<AeroFlightDTO.FlightData>> getFlightsByRoute(String originIata, String destIata, String date) {
		String fromTime = date + "T00:00";
		String toTime = date + "T12:00"; // 12-hour window to avoid limits

		return webClient.get()
				.uri(uriBuilder -> uriBuilder.path("/flights/airports/iata/{code}/{fromTime}/{toTime}")
						.queryParam("direction", "Departure").build(originIata, fromTime, toTime))
				.retrieve()
				.bodyToMono(AeroFlightDTO.Wrapper.class)
				.map(wrapper -> wrapper.getDepartures())
				.map(flights -> {
					List<AeroFlightDTO.FlightData> filtered = flights.stream()
							.filter(f -> f.getMovement() != null && f.getMovement().getAirport() != null
									&& destIata.equalsIgnoreCase(f.getMovement().getAirport().getIata()))
							.collect(Collectors.toList());

					if (filtered.isEmpty()) {
						return getMockFlights(originIata, destIata);
					}
					return filtered;
				})
				.onErrorReturn(getMockFlights(originIata, destIata));
	}

	private List<AeroFlightDTO.FlightData> getMockFlights(String from, String to) {
		List<AeroFlightDTO.FlightData> mocks = java.util.Collections.synchronizedList(new java.util.ArrayList<>());

		mocks.add(createMockFlight("AI-101", "Air India", to, "08:00", "09:30", "A320", "5400"));
		mocks.add(createMockFlight("6E-543", "IndiGo", to, "14:00", "15:45", "A321", "4800"));
		mocks.add(createMockFlight("UK-992", "Vistara", to, "19:00", "20:30", "B737", "6200"));

		return mocks;
	}

	private AeroFlightDTO.FlightData createMockFlight(String num, String air, String destIata, String depTime,
			String arrTime, String model, String price) {
		AeroFlightDTO.FlightData f = new AeroFlightDTO.FlightData();
		f.setNumber(num);
		f.setStatus("Scheduled");
		f.setPrice(price);
		f.setArrivalTime(arrTime);

		AeroFlightDTO.Airline airline = new AeroFlightDTO.Airline();
		airline.setName(air);
		f.setAirline(airline);

		AeroFlightDTO.Movement mov = new AeroFlightDTO.Movement();
		AeroFlightDTO.Airport airport = new AeroFlightDTO.Airport();
		airport.setIata(destIata);
		airport.setName("Destination Airport");
		mov.setAirport(airport);

		AeroFlightDTO.ScheduledTime stProxy = new AeroFlightDTO.ScheduledTime();
		stProxy.setLocal(depTime);
		mov.setScheduledTime(stProxy);

		mov.setTerminal("T2");
		f.setMovement(mov);

		AeroFlightDTO.Aircraft ac = new AeroFlightDTO.Aircraft();
		ac.setModel(model);
		f.setAircraft(ac);

		return f;
	}
}