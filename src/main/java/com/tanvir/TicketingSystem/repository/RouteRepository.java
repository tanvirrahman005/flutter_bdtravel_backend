package com.tanvir.TicketingSystem.repository;

import com.tanvir.TicketingSystem.entity.City;
import com.tanvir.TicketingSystem.entity.Route;
import com.tanvir.TicketingSystem.entity.TransportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    Optional<Route> findByRouteNumber(String routeNumber);

    List<Route> findByTransportType(TransportType transportType);

    List<Route> findByStartCityAndEndCity(City startCity, City endCity);

    List<Route> findByIsActiveTrue();

    @Query("SELECT r FROM Route r WHERE r.startCity.id = :startCityId AND r.endCity.id = :endCityId AND r.isActive = true")
    List<Route> findActiveRoutesBetweenCities(@Param("startCityId") Long startCityId,
            @Param("endCityId") Long endCityId);

    @Query("SELECT r FROM Route r WHERE r.transportType.id = :transportTypeId AND r.isActive = true")
    List<Route> findActiveRoutesByTransportType(@Param("transportTypeId") Long transportTypeId);

    boolean existsByRouteNumber(String routeNumber);

    @Query("SELECT COUNT(r) FROM Route r WHERE r.isActive = true")
    long countActiveRoutes();
}
