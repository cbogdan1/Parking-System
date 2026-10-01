package com.parcare.parking_system.repository;

import com.parcare.parking_system.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    // Gaseste toate masinile unui anumit proprietar
    List<Vehicle> findByOwnerId(Long ownerId);

    // Cauta dupa numarul de inmatriculare
    Optional<Vehicle> findByLicensePlate(String licensePlate);
}