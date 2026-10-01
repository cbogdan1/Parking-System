package com.parcare.parking_system.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicles")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String licensePlate;
    private String model;
    private String color;

    // Relatie: multe masini pot apartine unui singur utilizator
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    public Long getOwnerId() {
        if (this.owner != null) {
            return this.owner.getId();
        }
        return null;
    }
}