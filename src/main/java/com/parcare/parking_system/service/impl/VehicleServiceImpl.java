package com.parcare.parking_system.service.impl;

import com.parcare.parking_system.dto.VehicleDTO;
import com.parcare.parking_system.mapper.VehicleMapper;
import com.parcare.parking_system.model.User;
import com.parcare.parking_system.model.Vehicle;
import org.springframework.stereotype.Service;
import com.parcare.parking_system.repository.UserRepository;
import com.parcare.parking_system.repository.VehicleRepository;
import com.parcare.parking_system.service.VehicleService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleServiceImpl(VehicleRepository vehicleRepository,
                              UserRepository userRepository,
                              VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.vehicleMapper = vehicleMapper;
    }

    @Override
    public VehicleDTO addVehicle(VehicleDTO vehicleDTO) {
        User owner = null;
        if (vehicleDTO.getOwnerId() != null) {
            owner = userRepository.findById(vehicleDTO.getOwnerId())
                    .orElseThrow(() -> new NoSuchElementException("Proprietarul nu a fost gasit"));
        }
        Vehicle vehicle = vehicleMapper.toEntity(vehicleDTO, owner);
        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toDTO(saved);
    }

    @Override
    public List<VehicleDTO> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(vehicleMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleDTO getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Vehiculul nu a fost gasit"));
        return vehicleMapper.toDTO(vehicle);
    }

    @Override
    public VehicleDTO updateVehicle(Long id, VehicleDTO vehicleDTO) {
        if (!vehicleRepository.existsById(id)) {
            throw new NoSuchElementException("Vehiculul nu exista!");
        }
        User owner = null;
        if (vehicleDTO.getOwnerId() != null) {
            owner = userRepository.findById(vehicleDTO.getOwnerId())
                    .orElseThrow(() -> new NoSuchElementException("Proprietarul nu a fost gasit"));
        }
        Vehicle vehicle = vehicleMapper.toEntity(vehicleDTO, owner);
        vehicle.setId(id);
        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toDTO(saved);
    }

    @Override
    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }
}