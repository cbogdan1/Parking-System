package com.parcare.parking_system.controller;

import com.parcare.parking_system.dto.ReservationDTO;
import com.parcare.parking_system.dto.InvoiceXMLDTO;
import com.parcare.parking_system.dto.ReservationListWrapper;
import com.parcare.parking_system.dto.StatisticsDTO;
import com.parcare.parking_system.exporter.FileExporter;
import com.parcare.parking_system.exporter.TXTFileExporter;
import com.parcare.parking_system.exporter.XMLFileExporter;
import com.parcare.parking_system.mapper.ReservationMapper;
import com.parcare.parking_system.model.ParkingSpot;
import com.parcare.parking_system.model.Reservation;
import com.parcare.parking_system.model.User;
import com.parcare.parking_system.model.Vehicle;
import com.parcare.parking_system.repository.ParkingSpotRepository;
import com.parcare.parking_system.repository.UserRepository;
import com.parcare.parking_system.repository.VehicleRepository;
import com.parcare.parking_system.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;
    private final VehicleRepository vehicleRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final UserRepository userRepository;

    public ReservationController(ReservationService reservationService,
                                 ReservationMapper reservationMapper,
                                 VehicleRepository vehicleRepository,
                                 ParkingSpotRepository parkingSpotRepository,
                                 UserRepository userRepository) {
        this.reservationService = reservationService;
        this.reservationMapper = reservationMapper;
        this.vehicleRepository = vehicleRepository;
        this.parkingSpotRepository = parkingSpotRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAllReservations(@AuthenticationPrincipal UserDetails userDetails) {
        List<ReservationDTO> dtoList = reservationService.getAllReservations()
                .stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());

        // CLIENT vede doar rezervarile pentru masinile lui, ADMIN vede tot
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (currentUser != null && "ROLE_CLIENT".equals(currentUser.getRole())) {
            dtoList = dtoList.stream()
                    .filter(r -> currentUser.getId().equals(r.getVehicleOwnerId()))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
        Reservation reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservationMapper.toDTO(reservation));
    }

    @PostMapping
    public ResponseEntity<ReservationDTO> addReservation(@Valid @RequestBody ReservationDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new NoSuchElementException("Vehiculul nu a fost gasit!"));
        ParkingSpot spot = parkingSpotRepository.findById(dto.getParkingSpotId())
                .orElseThrow(() -> new NoSuchElementException("Locul de parcare nu a fost gasit!"));

        Reservation entity = reservationMapper.toEntity(dto, vehicle, spot);
        Reservation saved = reservationService.addReservation(entity);
        return ResponseEntity.ok(reservationMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationDTO> updateReservation(@PathVariable Long id, @Valid @RequestBody ReservationDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new NoSuchElementException("Vehiculul nu a fost gasit!"));
        ParkingSpot spot = parkingSpotRepository.findById(dto.getParkingSpotId())
                .orElseThrow(() -> new NoSuchElementException("Locul de parcare nu a fost gasit!"));

        dto.setId(id);
        Reservation entity = reservationMapper.toEntity(dto, vehicle, spot);
        Reservation updated = reservationService.updateReservation(entity);
        return ResponseEntity.ok(reservationMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<ReservationDTO> checkout(@PathVariable Long id) {
        Reservation reservation = reservationService.checkout(id);
        return ResponseEntity.ok(reservationMapper.toDTO(reservation));
    }

    @PostMapping("/{id}/extend")
    public ResponseEntity<ReservationDTO> extendReservation(@PathVariable Long id, @RequestParam int extraHours) {
        Reservation reservation = reservationService.extendReservation(id, extraHours);
        return ResponseEntity.ok(reservationMapper.toDTO(reservation));
    }

    // ===== GENERARE FACTURA/CHITANTA XML =====
    @GetMapping(value = "/{id}/invoice", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getInvoiceXML(@PathVariable Long id) {
        InvoiceXMLDTO invoice = reservationService.generateInvoiceXML(id);
        try {
            com.fasterxml.jackson.dataformat.xml.XmlMapper xmlMapper = new com.fasterxml.jackson.dataformat.xml.XmlMapper();
            String xmlContent = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(invoice);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=factura_rezervare_" + id + ".xml")
                    .body(xmlContent);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("<error>Eroare la generarea facturii XML</error>");
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsDTO> getStatistics() {
        return ResponseEntity.ok(reservationService.getGlobalStatistics());
    }

    // ===== STRATEGY PATTERN: Export rezervari =====
    @GetMapping("/export")
    public ResponseEntity<String> exportReservations(@RequestParam(defaultValue = "txt") String format) {
        List<ReservationDTO> dtoList = reservationService.getAllReservations()
                .stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());

        // Strategy Pattern - alegem strategia de export in functie de format
        FileExporter exporter;
        String contentType;
        String fileExtension;

        switch (format.toLowerCase()) {
            case "xml":
                exporter = new XMLFileExporter();
                contentType = MediaType.APPLICATION_XML_VALUE;
                fileExtension = "xml";
                break;
            case "txt":
            default:
                exporter = new TXTFileExporter();
                contentType = MediaType.TEXT_PLAIN_VALUE;
                fileExtension = "txt";
                break;
        }

        // Pentru XML, exportam wrapper-ul; pentru TXT, exportam lista direct
        String exportedData;
        if ("xml".equalsIgnoreCase(format)) {
            ReservationListWrapper wrapper = new ReservationListWrapper(dtoList);
            exportedData = exporter.exportData(wrapper);
        } else {
            StringBuilder sb = new StringBuilder();
            for (ReservationDTO dto : dtoList) {
                sb.append(exporter.exportData(dto)).append("\n");
            }
            exportedData = sb.toString();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reservations." + fileExtension)
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(exportedData);
    }
}