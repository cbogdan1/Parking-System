package com.parcare.parking_system.service;

import com.parcare.parking_system.model.Reservation;
import com.parcare.parking_system.repository.ReservationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ReservationNotificationService {

    private final ReservationRepository reservationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private final Set<Long> notifiedReservations = new HashSet<>();

    public ReservationNotificationService(ReservationRepository reservationRepository,
                                          SimpMessagingTemplate messagingTemplate) {
        this.reservationRepository = reservationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    // Ruleaza in fiecare minut (60000 ms)
    @Scheduled(fixedRate = 60000)
    public void checkExpiringReservations() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.plusMinutes(15);

        // Luam toate rezervarile din DB
        List<Reservation> activeReservations = reservationRepository.findAll();

        for (Reservation res : activeReservations) {
            // Verificam daca se afla in intervalul de 15 minute si daca e valabila
            if (res.getEndTime() != null &&
                res.getEndTime().isAfter(now) &&
                res.getEndTime().isBefore(threshold)) {

                // Daca nu a fost deja notificata
                if (!notifiedReservations.contains(res.getId())) {
                    
                    long minutesLeft = ChronoUnit.MINUTES.between(now, res.getEndTime());
                    
                    if (res.getVehicle() != null && res.getVehicle().getOwner() != null) {
                        Long ownerId = res.getVehicle().getOwner().getId();
                        String spotNum = res.getParkingSpot() != null ? res.getParkingSpot().getSpotNumber() : "?";

                        String message = "Rezervarea pentru locul " + spotNum + " expira in 15 minute!";

                        // Trimite mesaj pe topicul utilizatorului specific
                        messagingTemplate.convertAndSend("/topic/user/" + ownerId, message);
                        System.out.println("[WebSocket] Notificare trimisa catre User " + ownerId + " pentru rezervarea " + res.getId());
                    }

                    // Marcam ca notificata
                    notifiedReservations.add(res.getId());
                }
            }
        }
    }
}
