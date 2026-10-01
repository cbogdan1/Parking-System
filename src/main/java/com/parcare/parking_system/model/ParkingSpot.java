package com.parcare.parking_system.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parking_spots")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ParkingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String spotNumber;
    private String section;
    private Boolean occupied;
    private Double pricePerHour;
}