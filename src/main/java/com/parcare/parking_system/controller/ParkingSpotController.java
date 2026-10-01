package com.parcare.parking_system.controller;

import com.parcare.parking_system.dto.ParkingSpotDTO;
import com.parcare.parking_system.service.ParkingSpotService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spots")
@CrossOrigin(origins = "*")
public class ParkingSpotController {

    private final ParkingSpotService spotService;

    public ParkingSpotController(ParkingSpotService spotService) {
        this.spotService = spotService;
    }

    @GetMapping
    public ResponseEntity<List<ParkingSpotDTO>> getAllSpots() {
        return ResponseEntity.ok(spotService.getAllSpots());
    }

    @GetMapping("/available")
    public ResponseEntity<List<ParkingSpotDTO>> getAvailableSpots() {
        return ResponseEntity.ok(spotService.getAvailableSpots());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingSpotDTO> getSpotById(@PathVariable Long id) {
        return ResponseEntity.ok(spotService.getSpotById(id));
    }

    @PostMapping
    public ResponseEntity<ParkingSpotDTO> addSpot(@Valid @RequestBody ParkingSpotDTO spot) {
        return ResponseEntity.ok(spotService.addSpot(spot));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingSpotDTO> updateSpot(@PathVariable Long id, @Valid @RequestBody ParkingSpotDTO spot) {
        spot.setId(id);
        return ResponseEntity.ok(spotService.updateSpot(spot));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpot(@PathVariable Long id) {
        spotService.deleteSpot(id);
        return ResponseEntity.noContent().build();
    }
}