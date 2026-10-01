package com.parcare.parking_system.controller;

import com.parcare.parking_system.dto.StatisticsDTO;
import com.parcare.parking_system.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/stats")
@Tag(name = "Statistics", description = "Statistici si profituri")
public class StatisticsController {

    private final ReservationService reservationService;

    public StatisticsController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "Statistici globale", description = "Returneaza profit total, numar rezervari si locuri ocupate")
    @GetMapping
    public ResponseEntity<StatisticsDTO> getStats() {
        return ResponseEntity.ok(reservationService.getGlobalStatistics());
    }
}