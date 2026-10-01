package com.parcare.parking_system.repository;

import com.parcare.parking_system.model.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {
    //SELECT * FROM parking_spots WHERE occupied = ?;
    List<ParkingSpot> findByOccupied(Boolean occupied);
}