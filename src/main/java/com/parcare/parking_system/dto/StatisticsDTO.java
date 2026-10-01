package com.parcare.parking_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StatisticsDTO {
    private double totalProfit;
    private long totalReservations;
    private long occupiedSpotsCount;
}