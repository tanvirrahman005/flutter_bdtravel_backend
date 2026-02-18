package com.tanvir.TicketingSystem.repository;

import com.tanvir.TicketingSystem.entity.SeatLayout;
import com.tanvir.TicketingSystem.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatLayoutRepository extends JpaRepository<SeatLayout, Long> {
    List<SeatLayout> findByVehicle(Vehicle vehicle);
    List<SeatLayout> findByVehicleAndIsAvailableTrue(Vehicle vehicle);
    List<SeatLayout> findByVehicleAndSeatType(Vehicle vehicle, SeatLayout.SeatType seatType);
    List<SeatLayout> findByVehicleAndDeckLevel(Vehicle vehicle, SeatLayout.DeckLevel deckLevel);

    Optional<SeatLayout> findByVehicleAndSeatNumber(Vehicle vehicle, String seatNumber);

    @Query("SELECT sl FROM SeatLayout sl WHERE sl.vehicle.id = :vehicleId AND sl.isAvailable = true ORDER BY sl.seatNumber")
    List<SeatLayout> findAvailableSeatsByVehicleId(@Param("vehicleId") Long vehicleId);

    @Query("SELECT COUNT(sl) FROM SeatLayout sl WHERE sl.vehicle.id = :vehicleId AND sl.isAvailable = true")
    Long countAvailableSeatsByVehicleId(@Param("vehicleId") Long vehicleId);

    boolean existsByVehicleAndSeatNumber(Vehicle vehicle, String seatNumber);
}
