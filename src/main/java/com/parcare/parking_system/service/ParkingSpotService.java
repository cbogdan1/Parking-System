package com.parcare.parking_system.service;

import com.parcare.parking_system.dto.ParkingSpotDTO;
import com.parcare.parking_system.model.ParkingSpot;

import java.util.List;

public interface ParkingSpotService {
    ParkingSpotDTO addSpot(ParkingSpotDTO spot);
    List<ParkingSpotDTO> getAllSpots();
    List<ParkingSpotDTO> getAvailableSpots();
    ParkingSpotDTO getSpotById(Long id);
    ParkingSpotDTO updateSpot(ParkingSpotDTO spot); // Nou
    void deleteSpot(Long id);
}