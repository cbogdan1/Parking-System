package com.parcare.parking_system.service.impl;

import com.parcare.parking_system.model.ParkingSpot;
import com.parcare.parking_system.model.User;
import com.parcare.parking_system.model.Vehicle;
import com.parcare.parking_system.repository.ParkingSpotRepository;
import com.parcare.parking_system.repository.UserRepository;
import com.parcare.parking_system.repository.VehicleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MockDataServiceImpl {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingSpotRepository spotRepository;
    private final PasswordEncoder passwordEncoder;

    public MockDataServiceImpl(UserRepository userRepository,
                               VehicleRepository vehicleRepository,
                               ParkingSpotRepository spotRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.spotRepository = spotRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Transactional
    public void initData() {
        System.out.println("[MOCK DATA] Verific datele de test...");

        // Admin User
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .name("Administrator")
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .role("ROLE_ADMIN")
                    .email("admin@parcare.ro")
                    .build();
            userRepository.save(admin);
            System.out.println("[MOCK DATA] Cont ADMIN creat: admin / admin");
        }

        // Test User Andrei
        if (userRepository.findByUsername("andrei").isEmpty()) {
            User andrei = User.builder()
                    .name("Andrei Popescu")
                    .username("andrei")
                    .password(passwordEncoder.encode("parola"))
                    .role("ROLE_CLIENT")
                    .email("testemail@gmail.com")
                    .build();
            andrei = userRepository.save(andrei);

            Vehicle masinaAndrei = Vehicle.builder()
                    .licensePlate("CJ-99-BOS")
                    .model("Mercedes Benz")
                    .color("Negru")
                    .owner(andrei)
                    .build();
            vehicleRepository.save(masinaAndrei);
            System.out.println("[MOCK DATA] User ANDREI creat");
        }

        // Test User Bogdan
        if (userRepository.findByUsername("bogdan").isEmpty()) {
            User bogdan = User.builder()
                    .name("Bogdan Campean")
                    .username("bogdan")
                    .password(passwordEncoder.encode("parola"))
                    .role("ROLE_CLIENT")
                    .email("bogdan@test.ro")
                    .build();
            bogdan = userRepository.save(bogdan);

            Vehicle masinaBogdan1 = Vehicle.builder()
                    .licensePlate("B-101-ABC")
                    .model("BMW X5")
                    .color("Alb")
                    .owner(bogdan)
                    .build();
            
            Vehicle masinaBogdan2 = Vehicle.builder()
                    .licensePlate("CJ-55-XYZ")
                    .model("Audi A4")
                    .color("Gri")
                    .owner(bogdan)
                    .build();

            vehicleRepository.save(masinaBogdan1);
            vehicleRepository.save(masinaBogdan2);
            System.out.println("[MOCK DATA] User BOGDAN creat");
        }

        // Locuri de parcare
        if (spotRepository.count() == 0) {
            for (int i = 1; i <= 5; i++) {
                ParkingSpot spot = ParkingSpot.builder()
                        .spotNumber("A" + i)
                        .section("A")
                        .occupied(false)
                        .pricePerHour(10.0)
                        .build();
                spotRepository.save(spot);
            }
            for (int i = 1; i <= 3; i++) {
                ParkingSpot spot = ParkingSpot.builder()
                        .spotNumber("B" + i)
                        .section("B")
                        .occupied(false)
                        .pricePerHour(15.0)
                        .build();
                spotRepository.save(spot);
            }
            System.out.println("[MOCK DATA] Locuri de parcare generate!");
        }
    }
}
