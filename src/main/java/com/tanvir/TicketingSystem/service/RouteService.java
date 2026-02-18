package com.tanvir.TicketingSystem.service;

import com.tanvir.TicketingSystem.entity.Route;
import com.tanvir.TicketingSystem.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RouteService {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private CityService cityService;

    @Autowired
    private TransportTypeService transportTypeService;

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public List<Route> getActiveRoutes() {
        return routeRepository.findByIsActiveTrue();
    }

    public Optional<Route> getRouteById(Long id) {
        return routeRepository.findById(id);
    }

    public Optional<Route> getRouteByNumber(String routeNumber) {
        return routeRepository.findByRouteNumber(routeNumber);
    }

    public List<Route> getRoutesBetweenCities(Long startCityId, Long endCityId) {
        return routeRepository.findActiveRoutesBetweenCities(startCityId, endCityId);
    }

    public List<Route> getRoutesByTransportType(Long transportTypeId) {
        return routeRepository.findActiveRoutesByTransportType(transportTypeId);
    }

    public Route createRoute(Route route) {
        return routeRepository.save(route);
    }

    public Route updateRoute(Long id, Route routeDetails) {
        Optional<Route> optionalRoute = routeRepository.findById(id);
        if (optionalRoute.isPresent()) {
            Route route = optionalRoute.get();
            route.setRouteNumber(routeDetails.getRouteNumber());
            route.setTransportType(routeDetails.getTransportType());
            route.setStartCity(routeDetails.getStartCity());
            route.setEndCity(routeDetails.getEndCity());
            route.setDistanceKm(routeDetails.getDistanceKm());
            route.setEstimatedDurationMinutes(routeDetails.getEstimatedDurationMinutes());
            route.setIsActive(routeDetails.getIsActive());
            return routeRepository.save(route);
        }
        return null;
    }

    public void deactivateRoute(Long id) {
        Optional<Route> optionalRoute = routeRepository.findById(id);
        if (optionalRoute.isPresent()) {
            Route route = optionalRoute.get();
            route.setIsActive(false);
            routeRepository.save(route);
        }
    }

    public void deleteRoute(Long id) {
        routeRepository.deleteById(id);
    }

    public boolean existsByRouteNumber(String routeNumber) {
        return routeRepository.existsByRouteNumber(routeNumber);
    }
}
