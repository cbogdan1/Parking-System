package com.parcare.parking_system.service.impl;

import com.parcare.parking_system.dto.ParkingSpotDTO;
import com.parcare.parking_system.mapper.ParkingSpotMapper;
import com.parcare.parking_system.model.ParkingSpot;
import com.parcare.parking_system.model.Reservation;
import org.springframework.stereotype.Service;
import com.parcare.parking_system.repository.ParkingSpotRepository;
import com.parcare.parking_system.repository.ReservationRepository;
import com.parcare.parking_system.service.ParkingSpotService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ParkingSpotServiceImpl implements ParkingSpotService {

    private final ParkingSpotRepository spotRepository;
    private final ReservationRepository reservationRepository;
    private final ParkingSpotMapper parkingSpotMapper;

    public ParkingSpotServiceImpl(ParkingSpotRepository spotRepository,
                                  ReservationRepository reservationRepository,
                                  ParkingSpotMapper parkingSpotMapper) {
        this.spotRepository = spotRepository;
        this.reservationRepository = reservationRepository;
        this.parkingSpotMapper = parkingSpotMapper;
    }

    @Override
    public ParkingSpotDTO addSpot(ParkingSpotDTO spotDTO) {
        ParkingSpot entity = parkingSpotMapper.toEntity(spotDTO);
        ParkingSpot saved = spotRepository.save(entity);
        return parkingSpotMapper.toDTO(saved);
    }

    @Override
    public List<ParkingSpotDTO> getAllSpots() {
        return spotRepository.findAll().stream()
                .map(parkingSpotMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParkingSpotDTO> getAvailableSpots() {
        // Folosim metoda pe care ai definit-o in ParkingSpotRepository
        return spotRepository.findByOccupied(false).stream()
                .map(parkingSpotMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ParkingSpotDTO getSpotById(Long id) {
        ParkingSpot spot = spotRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Locul de parcare cu ID-ul " + id + " nu a fost gasit!"));
        return parkingSpotMapper.toDTO(spot);
    }

    @Override
    public void deleteSpot(Long id) {
        ParkingSpot spot = spotRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Locul de parcare cu ID-ul " + id + " nu a fost gasit!"));

        if (Boolean.TRUE.equals(spot.getOccupied())) {
            throw new IllegalStateException("Locul de parcare " + spot.getSpotNumber() + " este ocupat si nu poate fi sters!");
        }

        // Stergem rezervarile vechi/finalizate asociate cu acest loc
        List<Reservation> reservations = reservationRepository.findAll().stream()
                .filter(r -> r.getParkingSpot() != null && r.getParkingSpot().getId().equals(id))
                .toList();

        if (!reservations.isEmpty()) {
            reservationRepository.deleteAll(reservations);
        }

        spotRepository.deleteById(id);
    }

    @Override
    public ParkingSpotDTO updateSpot(ParkingSpotDTO spotDTO) {
        if (!spotRepository.existsById(spotDTO.getId())) {
            throw new NoSuchElementException("Locul nu exista!");
        }
        ParkingSpot entity = parkingSpotMapper.toEntity(spotDTO);
        ParkingSpot saved = spotRepository.save(entity);
        return parkingSpotMapper.toDTO(saved);
    }
}