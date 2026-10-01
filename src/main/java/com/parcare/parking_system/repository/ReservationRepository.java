package com.parcare.parking_system.repository;

import com.parcare.parking_system.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByParkingSpot_Section(String section);
    List<Reservation> findByEndTimeBeforeAndParkingSpot_OccupiedTrue(LocalDateTime time);
}