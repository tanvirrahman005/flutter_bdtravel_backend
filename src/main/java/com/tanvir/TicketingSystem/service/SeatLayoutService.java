package com.tanvir.TicketingSystem.service;

import com.tanvir.TicketingSystem.entity.SeatLayout;
import com.tanvir.TicketingSystem.entity.Vehicle;
import com.tanvir.TicketingSystem.repository.SeatLayoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeatLayoutService {

    @Autowired
    private SeatLayoutRepository seatLayoutRepository;

    @Autowired
    private VehicleService vehicleService;

    public List<SeatLayout> getSeatsByVehicle(Long vehicleId) {
        Optional<Vehicle> vehicle = vehicleService.getVehicleById(vehicleId);
        return vehicle.map(seatLayoutRepository::findByVehicle).orElse(List.of());
    }

    public List<SeatLayout> getAvailableSeatsByVehicle(Long vehicleId) {
        return seatLayoutRepository.findAvailableSeatsByVehicleId(vehicleId);
    }

    public List<SeatLayout> getAllSeats() {
        return seatLayoutRepository.findAll();
    }

    public Optional<SeatLayout> getSeatById(Long id) {
        return seatLayoutRepository.findById(id);
    }

    public Optional<SeatLayout> getSeatByVehicleAndNumber(Long vehicleId, String seatNumber) {
        Optional<Vehicle> vehicle = vehicleService.getVehicleById(vehicleId);
        return vehicle.flatMap(v -> seatLayoutRepository.findByVehicleAndSeatNumber(v, seatNumber));
    }

    public SeatLayout createSeat(SeatLayout seatLayout) {
        return seatLayoutRepository.save(seatLayout);
    }

    public List<SeatLayout> createMultipleSeats(List<SeatLayout> seatLayouts) {
        return seatLayoutRepository.saveAll(seatLayouts);
    }

    public SeatLayout updateSeat(Long id, SeatLayout seatDetails) {
        Optional<SeatLayout> optionalSeat = seatLayoutRepository.findById(id);
        if (optionalSeat.isPresent()) {
            SeatLayout seat = optionalSeat.get();
            seat.setSeatNumber(seatDetails.getSeatNumber());
            seat.setSeatType(seatDetails.getSeatType());
            seat.setDeckLevel(seatDetails.getDeckLevel());
            seat.setRowPosition(seatDetails.getRowPosition());
            seat.setColumnPosition(seatDetails.getColumnPosition());
            seat.setIsAvailable(seatDetails.getIsAvailable());
            if (seatDetails.getVehicle() != null) {
                seat.setVehicle(seatDetails.getVehicle());
            }
            return seatLayoutRepository.save(seat);
        }
        return null;
    }

    public SeatLayout updateSeatAvailability(Long seatId, boolean isAvailable) {
        Optional<SeatLayout> optionalSeat = seatLayoutRepository.findById(seatId);
        if (optionalSeat.isPresent()) {
            SeatLayout seat = optionalSeat.get();
            seat.setIsAvailable(isAvailable);
            return seatLayoutRepository.save(seat);
        }
        return null;
    }

    public void deleteSeat(Long id) {
        seatLayoutRepository.deleteById(id);
    }

    public Long countAvailableSeatsByVehicle(Long vehicleId) {
        return seatLayoutRepository.countAvailableSeatsByVehicleId(vehicleId);
    }

    public boolean isSeatAvailable(Long seatId) {
        Optional<SeatLayout> seat = seatLayoutRepository.findById(seatId);
        return seat.map(SeatLayout::getIsAvailable).orElse(false);
    }
}
