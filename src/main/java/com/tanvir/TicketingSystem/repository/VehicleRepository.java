package com.tanvir.TicketingSystem.repository;

import com.tanvir.TicketingSystem.entity.TransportCompany;
import com.tanvir.TicketingSystem.entity.TransportType;
import com.tanvir.TicketingSystem.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);

    List<Vehicle> findByTransportCompany(TransportCompany transportCompany);

    List<Vehicle> findByTransportType(TransportType transportType);

    List<Vehicle> findByIsActiveTrue();

    List<Vehicle> findByTransportCompanyAndIsActiveTrue(TransportCompany transportCompany);

    @Query("SELECT v FROM Vehicle v WHERE v.transportCompany.id = :companyId AND v.isActive = true")
    List<Vehicle> findActiveVehiclesByCompanyId(@Param("companyId") Long companyId);

    boolean existsByVehicleNumber(String vehicleNumber);

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.isActive = true")
    long countActiveVehicles();
}
