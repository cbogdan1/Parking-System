package com.parcare.parking_system.service;

import com.parcare.parking_system.dto.InvoiceXMLDTO;
import com.parcare.parking_system.dto.StatisticsDTO;
import com.parcare.parking_system.model.Reservation;

import java.util.List;
public interface ReservationService {
    Reservation addReservation(Reservation reservation);
    List<Reservation> getAllReservations();
    Reservation getReservationById(Long id); // Nou
    Reservation updateReservation(Reservation reservation); // Nou
    void finalizeReservation(Long id);
    void deleteReservation(Long id);
    StatisticsDTO getGlobalStatistics();
    Reservation checkout(Long id);
    Reservation extendReservation(Long id, int extraHours);
    InvoiceXMLDTO generateInvoiceXML(Long reservationId);
}