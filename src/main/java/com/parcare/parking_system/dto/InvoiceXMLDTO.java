package com.parcare.parking_system.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pentru generarea facturii/chitantei in format XML.
 *
 * Structura XML generata:
 * <Invoice>
 *     <Id>1</Id>
 *     <ClientName>Bogdan Campean</ClientName>
 *     <VehiclePlate>B-123-ABC</VehiclePlate>
 *     <SpotNumber>A1</SpotNumber>
 *     <SpotSection>A</SpotSection>
 *     <DurationHours>3</DurationHours>
 *     <PricePerHour>5.0</PricePerHour>
 *     <TotalCost>15.0</TotalCost>
 *     <StartTime>2026-05-24T10:00:00</StartTime>
 *     <EndTime>2026-05-24T13:00:00</EndTime>
 *     <Timestamp>2026-05-24T13:05:00</Timestamp>
 * </Invoice>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "Invoice")
public class InvoiceXMLDTO {

    @JacksonXmlProperty(localName = "ClientName")
    private String clientName;

    @JacksonXmlProperty(localName = "ClientEmail")
    private String clientEmail;

    @JacksonXmlProperty(localName = "VehiclePlate")
    private String vehiclePlate;

    @JacksonXmlProperty(localName = "VehicleModel")
    private String vehicleModel;

    @JacksonXmlProperty(localName = "SpotNumber")
    private String spotNumber;

    @JacksonXmlProperty(localName = "SpotSection")
    private String spotSection;

    @JacksonXmlProperty(localName = "DurationHours")
    private Long durationHours;

    @JacksonXmlProperty(localName = "PricePerHour")
    private Double pricePerHour;

    @JacksonXmlProperty(localName = "TotalCost")
    private Double totalCost;

    @JacksonXmlProperty(localName = "StartTime")
    private String startTime;

    @JacksonXmlProperty(localName = "EndTime")
    private String endTime;

    @JacksonXmlProperty(localName = "Timestamp")
    private String timestamp;
}
