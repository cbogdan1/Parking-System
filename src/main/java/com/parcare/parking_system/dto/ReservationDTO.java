package com.parcare.parking_system.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "reservation")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReservationDTO {
    private Long id;

    @NotNull(message = "Trebuie sa selectezi un vehicul!")
    private Long vehicleId;

    @NotNull(message = "Trebuie sa selectezi un loc de parcare!")
    private Long parkingSpotId;

    @NotNull(message = "Data de inceput este obligatorie!")
    private LocalDateTime startTime;

    @NotNull(message = "Data de sfarsit este obligatorie!")
    private LocalDateTime endTime;

    private Double totalCost;
    
    // Optional: Campuri suplimentare pentru afisare mai facila pe frontend
    private String vehicleLicensePlate;
    private String vehicleModel;
    private Long vehicleOwnerId;
    private String spotNumber;
    private String spotSection;

    @Override
    public String toString() {
        return "Rezervare #" + id +
                " | Loc: " + spotNumber + " (" + spotSection + ")" +
                " | Vehicul: " + vehicleLicensePlate + " (" + vehicleModel + ")" +
                " | Start: " + startTime +
                " | End: " + endTime +
                " | Cost: " + totalCost + " RON";
    }
}
