package com.parcare.parking_system.model;

import jakarta.persistence.*; // Importuri noi pentru baza de date
import lombok.*;
import java.util.List;

@Entity
@Table(name = "users") // Numele tabelului in MySQL
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User { // Am eliminat Serializable conform planului tau

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID auto-incrementat de baza de date
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;
    private String role;
    private String email;

    // Relatie: un user poate avea mai multe masini
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Vehicle> vehicles;
}