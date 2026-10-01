package com.parcare.parking_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpotDTO {
    private Long id;

    @NotBlank(message = "Numarul locului este obligatoriu!")
    private String spotNumber;

    @NotBlank(message = "Sectiunea este obligatorie!")
    private String section;

    private Boolean occupied;

    @NotNull(message = "Pretul este obligatoriu!")
    @Min(value = 0, message = "Pretul nu poate fi negativ!")
    private Double pricePerHour;
}

