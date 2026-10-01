package com.parcare.parking_system.mapper;

import com.parcare.parking_system.dto.ParkingSpotDTO;
import com.parcare.parking_system.model.ParkingSpot;
import org.springframework.stereotype.Component;

@Component
public class ParkingSpotMapper {

    public ParkingSpotDTO toDTO(ParkingSpot spot) {
        if (spot == null) {
            return null;
        }

        return new ParkingSpotDTO(
                spot.getId(),
                spot.getSpotNumber(),
                spot.getSection(),
                spot.getOccupied(),
                spot.getPricePerHour()
        );
    }

    public ParkingSpot toEntity(ParkingSpotDTO dto) {
        if (dto == null) {
            return null;
        }

        return ParkingSpot.builder()
                .id(dto.getId())
                .spotNumber(dto.getSpotNumber())
                .section(dto.getSection())
                .occupied(dto.getOccupied())
                .pricePerHour(dto.getPricePerHour())
                .build();
    }
}
