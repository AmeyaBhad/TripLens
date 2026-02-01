package com.org.Triplens.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.Triplens.entity.Hotel;
import com.org.Triplens.repository.HotelRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    @Autowired
    private HotelRepository hotelRepository;

    @GetMapping("/health")
    public String healthCheck() {
        return "Hotel Controller is running!";
    }

    @PostMapping
    public Hotel saveHotel(@RequestBody Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    @GetMapping
    public java.util.List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    @GetMapping("/{city}")
    public org.springframework.http.ResponseEntity<?> getHotels(@PathVariable String city) {
        try {
            return org.springframework.http.ResponseEntity.ok(hotelRepository.findByCity(city));
        } catch (Exception e) {
            e.printStackTrace();
            return org.springframework.http.ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
