package com.parcare.parking_system.service;

import com.parcare.parking_system.model.ParkingSpot;
import com.parcare.parking_system.model.Reservation;
import com.parcare.parking_system.model.Vehicle;
import java.util.List;

public interface MockDataService {
    void generateMockData();
    List<Vehicle> getVehicles();
    List<ParkingSpot> getSpots();
    List<Reservation> getReservations();
}