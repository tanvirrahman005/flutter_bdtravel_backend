package com.tanvir.TicketingSystem.service;

import com.tanvir.TicketingSystem.entity.TransportType;
import com.tanvir.TicketingSystem.entity.Vehicle;
import com.tanvir.TicketingSystem.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private TransportCompanyService transportCompanyService;

    @Autowired
    private TransportTypeService transportTypeService;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> getActiveVehicles() {
        return vehicleRepository.findByIsActiveTrue();
    }

    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    public Optional<Vehicle> getVehicleByNumber(String vehicleNumber) {
        return vehicleRepository.findByVehicleNumber(vehicleNumber);
    }

    public List<Vehicle> getVehiclesByCompany(Long companyId) {
        return vehicleRepository.findActiveVehiclesByCompanyId(companyId);
    }

    public List<Vehicle> getVehiclesByTransportType(Long transportTypeId) {
        Optional<TransportType> transportType = transportTypeService.getTransportTypeById(transportTypeId);
        return transportType.map(vehicleRepository::findByTransportType).orElse(List.of());
    }

    public Vehicle createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicle(Long id, Vehicle vehicleDetails) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(id);
        if (optionalVehicle.isPresent()) {
            Vehicle vehicle = optionalVehicle.get();
            vehicle.setVehicleNumber(vehicleDetails.getVehicleNumber());
            vehicle.setModel(vehicleDetails.getModel());
            vehicle.setTotalSeats(vehicleDetails.getTotalSeats());
            vehicle.setFacilities(vehicleDetails.getFacilities());
            vehicle.setIsActive(vehicleDetails.getIsActive());
            vehicle.setVehicleType(vehicleDetails.getVehicleType());

            if (vehicleDetails.getTransportCompany() != null) {
                vehicle.setTransportCompany(vehicleDetails.getTransportCompany());
            }
            if (vehicleDetails.getTransportType() != null) {
                vehicle.setTransportType(vehicleDetails.getTransportType());
            }

            return vehicleRepository.save(vehicle);
        }
        return null;
    }

    public Vehicle deactivateVehicle(Long id) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(id);
        if (optionalVehicle.isPresent()) {
            Vehicle vehicle = optionalVehicle.get();
            vehicle.setIsActive(false);
            return vehicleRepository.save(vehicle);
        }
        return null;
    }

    public Vehicle activateVehicle(Long id) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(id);
        if (optionalVehicle.isPresent()) {
            Vehicle vehicle = optionalVehicle.get();
            vehicle.setIsActive(true);
            return vehicleRepository.save(vehicle);
        }
        return null;
    }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }

    public boolean existsByVehicleNumber(String vehicleNumber) {
        return vehicleRepository.existsByVehicleNumber(vehicleNumber);
    }
}
