package com.parcare.parking_system.dto;

import com.parcare.parking_system.validators.ValidLicensePlate;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private Long id;

    @ValidLicensePlate
    private String licensePlate;

    @NotBlank(message = "Campul este obligatoriu")
    private String model;

    @NotBlank(message = "Campul este obligatoriu")
    private String color;

    private Long ownerId;
    private String ownerName;
}
