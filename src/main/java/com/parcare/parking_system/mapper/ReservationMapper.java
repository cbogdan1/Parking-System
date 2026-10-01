package com.parcare.parking_system.mapper;

import com.parcare.parking_system.dto.ReservationDTO;
import com.parcare.parking_system.model.ParkingSpot;
import com.parcare.parking_system.model.Reservation;
import com.parcare.parking_system.model.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public ReservationDTO toDTO(Reservation reservation) {
        if (reservation == null) {
            return null;
        }

        return ReservationDTO.builder()
                .id(reservation.getId())
                .vehicleId(reservation.getVehicle() != null ? reservation.getVehicle().getId() : null)
                .parkingSpotId(reservation.getParkingSpot() != null ? reservation.getParkingSpot().getId() : null)
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .totalCost(reservation.getTotalCost())
                .vehicleLicensePlate(reservation.getVehicle() != null ? reservation.getVehicle().getLicensePlate() : null)
                .vehicleModel(reservation.getVehicle() != null ? reservation.getVehicle().getModel() : null)
                .vehicleOwnerId((reservation.getVehicle() != null && reservation.getVehicle().getOwner() != null) ? reservation.getVehicle().getOwner().getId() : null)
                .spotNumber(reservation.getParkingSpot() != null ? reservation.getParkingSpot().getSpotNumber() : null)
                .spotSection(reservation.getParkingSpot() != null ? reservation.getParkingSpot().getSection() : null)
                .build();
    }

    public Reservation toEntity(ReservationDTO dto, Vehicle vehicle, ParkingSpot parkingSpot) {
        if (dto == null) {
            return null;
        }

        return Reservation.builder()
                .id(dto.getId())
                .vehicle(vehicle)
                .parkingSpot(parkingSpot)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .totalCost(dto.getTotalCost())
                .build();
    }
}
