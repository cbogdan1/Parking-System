package com.parcare.parking_system.service.impl;

import com.parcare.parking_system.dto.InvoiceXMLDTO;
import com.parcare.parking_system.dto.StatisticsDTO;
import com.parcare.parking_system.model.ParkingSpot;
import com.parcare.parking_system.model.Reservation;
import com.parcare.parking_system.repository.ParkingSpotRepository;
import com.parcare.parking_system.repository.ReservationRepository;
import com.parcare.parking_system.service.ReservationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ParkingSpotRepository spotRepository;
    private final EmailService emailService;

    // Am scos UserRepository si VehicleRepository pentru ca JPA face legaturile automat acum
    // prin relatiile @ManyToOne din clasele tale model.

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  ParkingSpotRepository spotRepository,
                                  EmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.spotRepository = spotRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public Reservation addReservation(Reservation reservation) {
        ParkingSpot spot = spotRepository.findById(reservation.getParkingSpot().getId())
                .orElseThrow(() -> new NoSuchElementException("Locul de parcare nu a fost gasit!"));

        if (spot.getOccupied()) {
            throw new IllegalStateException("Locul de parcare este deja ocupat!");
        }

        long hours = Math.max(1, Duration.between(reservation.getStartTime(), reservation.getEndTime()).toHours());
        reservation.setTotalCost(hours * spot.getPricePerHour());

        spot.setOccupied(true);
        spotRepository.save(spot);

        Reservation savedReservation = reservationRepository.save(reservation);

        // Trimitere mail - Acum accesam direct obiectul Owner prin JPA
        try {
            if (reservation.getVehicle() != null && reservation.getVehicle().getOwner() != null) {
                String toEmail = reservation.getVehicle().getOwner().getEmail();
                String subject = "Confirmare Rezervare Parcare - Locul " + spot.getSpotNumber();
                String body = "Salut,\n\nRezervarea ta a fost inregistrata cu succes!\n" +
                        "Cost total: " + reservation.getTotalCost() + " RON\n\nMultumim!";
                emailService.sendEmail(toEmail, subject, body);
            }
        } catch (Exception e) {
            System.err.println("Eroare la trimiterea mail-ului: " + e.getMessage());
        }

        return savedReservation;
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rezervarea nu a fost gasita!"));
    }

    @Override
    @Transactional
    public void deleteReservation(Long id) {
        Reservation res = getReservationById(id);
        ParkingSpot spot = res.getParkingSpot();
        spot.setOccupied(false);
        spotRepository.save(spot);
        reservationRepository.deleteById(id);
    }

    @Override
    public Reservation updateReservation(Reservation reservation) {
        if (!reservationRepository.existsById(reservation.getId())) {
            throw new NoSuchElementException("Rezervarea nu exista!");
        }
        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public void finalizeReservation(Long id) {
        Reservation res = getReservationById(id);
        ParkingSpot spot = res.getParkingSpot();
        spot.setOccupied(false);
        spotRepository.save(spot);
    }

    @Override
    public StatisticsDTO getGlobalStatistics() {
        List<Reservation> allRes = reservationRepository.findAll();
        double profit = allRes.stream().mapToDouble(Reservation::getTotalCost).sum();
        long count = allRes.size();

        // Numaram locurile ocupate direct din DB
        long occupiedSpots = spotRepository.findAll().stream()
                .filter(spot -> spot.getOccupied() != null && spot.getOccupied())
                .count();

        return new StatisticsDTO(profit, count, occupiedSpots);
    }

    @Override
    @Transactional
    public Reservation checkout(Long id) {
        Reservation res = getReservationById(id);
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(res.getStartTime())) {
            now = res.getStartTime().plusMinutes(1);
        }

        long initialHours = Math.max(1, Duration.between(res.getStartTime(), res.getEndTime()).toHours());
        double lockedPricePerHour = res.getTotalCost() / initialHours;

        res.setEndTime(now);
        long hours = Math.max(1, Duration.between(res.getStartTime(), now).toHours());
        res.setTotalCost(hours * lockedPricePerHour);

        ParkingSpot spot = res.getParkingSpot();
        spot.setOccupied(false);
        spotRepository.save(spot);

        return reservationRepository.save(res);
    }

    @Override
    @Transactional
    public Reservation extendReservation(Long id, int extraHours) {
        Reservation res = getReservationById(id);

        long initialHours = Math.max(1, Duration.between(res.getStartTime(), res.getEndTime()).toHours());
        double lockedPricePerHour = res.getTotalCost() / initialHours;

        LocalDateTime newEndTime = res.getEndTime().plusHours(extraHours);
        res.setEndTime(newEndTime);

        long totalHours = Math.max(1, Duration.between(res.getStartTime(), newEndTime).toHours());
        res.setTotalCost(totalHours * lockedPricePerHour);

        Reservation updated = reservationRepository.save(res);

        try {
            if (res.getVehicle() != null && res.getVehicle().getOwner() != null) {
                String toEmail = res.getVehicle().getOwner().getEmail();
                String subject = "Confirmare extindere rezervare #" + res.getId();
                String body = "Rezervarea a fost extinsa cu " + extraHours + " ore.\nCost nou: " + updated.getTotalCost() + " RON.";
                emailService.sendEmail(toEmail, subject, body);
            }
        } catch (Exception e) {
            System.err.println("Eroare mail extindere: " + e.getMessage());
        }

        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceXMLDTO generateInvoiceXML(Long reservationId) {
        Reservation res = getReservationById(reservationId);

        // Extragem informatiile necesare pentru factura
        String clientName = "Necunoscut";
        String clientEmail = "";
        String vehiclePlate = "N/A";
        String vehicleModel = "N/A";

        if (res.getVehicle() != null) {
            vehiclePlate = res.getVehicle().getLicensePlate();
            vehicleModel = res.getVehicle().getModel();
            if (res.getVehicle().getOwner() != null) {
                clientName = res.getVehicle().getOwner().getName();
                clientEmail = res.getVehicle().getOwner().getEmail();
            }
        }

        String spotNumber = "N/A";
        String spotSection = "N/A";
        Double pricePerHour = 0.0;

        if (res.getParkingSpot() != null) {
            spotNumber = res.getParkingSpot().getSpotNumber();
            spotSection = res.getParkingSpot().getSection();
            pricePerHour = res.getParkingSpot().getPricePerHour();
        }

        // Calculam durata in ore
        long durationHours = Math.max(1, Duration.between(res.getStartTime(), res.getEndTime()).toHours());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return InvoiceXMLDTO.builder()
                .clientName(clientName)
                .clientEmail(clientEmail)
                .vehiclePlate(vehiclePlate)
                .vehicleModel(vehicleModel)
                .spotNumber(spotNumber)
                .spotSection(spotSection)
                .durationHours(durationHours)
                .pricePerHour(pricePerHour)
                .totalCost(res.getTotalCost())
                .startTime(res.getStartTime().format(formatter))
                .endTime(res.getEndTime().format(formatter))
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
    }
}