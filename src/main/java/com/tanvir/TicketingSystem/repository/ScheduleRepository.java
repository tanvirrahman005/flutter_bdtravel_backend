package com.tanvir.TicketingSystem.repository;

import com.tanvir.TicketingSystem.entity.Route;
import com.tanvir.TicketingSystem.entity.Schedule;
import com.tanvir.TicketingSystem.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByRoute(Route route);

    List<Schedule> findByVehicle(Vehicle vehicle);

    List<Schedule> findByStatus(Schedule.ScheduleStatus status);

    List<Schedule> findByDepartureTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT s FROM Schedule s WHERE s.route.id = :routeId AND s.departureTime >= :startTime AND s.status = 'SCHEDULED' ORDER BY s.departureTime ASC")
    List<Schedule> findUpcomingSchedulesByRoute(@Param("routeId") Long routeId,
            @Param("startTime") LocalDateTime startTime);

    @Query("SELECT s FROM Schedule s WHERE s.route.startCity.id = :startCityId AND s.route.endCity.id = :endCityId AND s.departureTime >= :departureDate AND s.status = 'SCHEDULED' "
            +
            "AND (:transportTypeId IS NULL OR s.vehicle.transportType.id = :transportTypeId) ORDER BY s.departureTime ASC")
    List<Schedule> findAvailableSchedules(@Param("startCityId") Long startCityId,
            @Param("endCityId") Long endCityId,
            @Param("departureDate") LocalDateTime departureDate,
            @Param("transportTypeId") Long transportTypeId);

    @Query("SELECT s FROM Schedule s WHERE s.vehicle.transportType.id = :transportTypeId AND s.departureTime >= :startDate AND s.status = 'SCHEDULED'")
    List<Schedule> findSchedulesByTransportTypeAndDate(@Param("transportTypeId") Long transportTypeId,
            @Param("startDate") LocalDateTime startDate);
}
