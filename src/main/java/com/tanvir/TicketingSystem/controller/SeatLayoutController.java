package com.tanvir.TicketingSystem.controller;

import com.tanvir.TicketingSystem.entity.SeatLayout;
import com.tanvir.TicketingSystem.service.SeatLayoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@CrossOrigin(origins = "*")
public class SeatLayoutController {

    @Autowired
    private SeatLayoutService seatLayoutService;

    @GetMapping("/vehicle/{vehicleId}")
    public List<SeatLayout> getSeatsByVehicle(@PathVariable Long vehicleId) {
        return seatLayoutService.getSeatsByVehicle(vehicleId);
    }

    @GetMapping("/vehicle/{vehicleId}/available")
    public List<SeatLayout> getAvailableSeatsByVehicle(@PathVariable Long vehicleId) {
        return seatLayoutService.getAvailableSeatsByVehicle(vehicleId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeatLayout> getSeatById(@PathVariable Long id) {
        return seatLayoutService.getSeatById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/vehicle/{vehicleId}/seat/{seatNumber}")
    public ResponseEntity<SeatLayout> getSeatByVehicleAndNumber(
            @PathVariable Long vehicleId,
            @PathVariable String seatNumber) {
        return seatLayoutService.getSeatByVehicleAndNumber(vehicleId, seatNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public SeatLayout createSeat(@RequestBody SeatLayout seatLayout) {
        return seatLayoutService.createSeat(seatLayout);
    }

    @PostMapping("/bulk")
    public List<SeatLayout> createMultipleSeats(@RequestBody List<SeatLayout> seatLayouts) {
        return seatLayoutService.createMultipleSeats(seatLayouts);
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<Void> updateSeatAvailability(@PathVariable Long id, @RequestParam boolean available) {
        seatLayoutService.updateSeatAvailability(id, available);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<SeatLayout> getAllSeats() {
        return seatLayoutService.getAllSeats();
    }

    @GetMapping("/vehicle/{vehicleId}/available-count")
    public Long getAvailableSeatsCount(@PathVariable Long vehicleId) {
        return seatLayoutService.countAvailableSeatsByVehicle(vehicleId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeatLayout> updateSeat(@PathVariable Long id, @RequestBody SeatLayout seatLayout) {
        SeatLayout updated = seatLayoutService.updateSeat(id, seatLayout);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeat(@PathVariable Long id) {
        seatLayoutService.deleteSeat(id);
        return ResponseEntity.ok().build();
    }
}
