package com.parcare.parking_system.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "reservations")
public class ReservationListWrapper {

    private List<ReservationDTO> reservations;

    public ReservationListWrapper() {
    }

    public ReservationListWrapper(List<ReservationDTO> reservations) {
        this.reservations = reservations;
    }

    @XmlElement(name = "reservation")
    public List<ReservationDTO> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationDTO> reservations) {
        this.reservations = reservations;
    }
}
