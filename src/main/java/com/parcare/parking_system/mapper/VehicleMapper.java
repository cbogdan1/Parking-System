package com.parcare.parking_system.mapper;

import com.parcare.parking_system.dto.VehicleDTO;
import com.parcare.parking_system.model.User;
import com.parcare.parking_system.model.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public VehicleDTO toDTO(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }

        return new VehicleDTO(
                vehicle.getId(),
                vehicle.getLicensePlate(),
                vehicle.getModel(),
                vehicle.getColor(),
                vehicle.getOwner() != null ? vehicle.getOwner().getId() : null,
                vehicle.getOwner() != null ? vehicle.getOwner().getName() : null
        );
    }

    public Vehicle toEntity(VehicleDTO dto, User owner) {
        if (dto == null) {
            return null;
        }

        return Vehicle.builder()
                .id(dto.getId())
                .licensePlate(dto.getLicensePlate())
                .model(dto.getModel())
                .color(dto.getColor())
                .owner(owner)
                .build();
    }
}
