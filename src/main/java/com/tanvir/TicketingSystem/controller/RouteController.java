package com.tanvir.TicketingSystem.controller;

import com.tanvir.TicketingSystem.entity.Route;
import com.tanvir.TicketingSystem.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@CrossOrigin(origins = "*")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping
    public List<Route> getAllRoutes() {
        return routeService.getAllRoutes();
    }

    @GetMapping("/active")
    public List<Route> getActiveRoutes() {
        return routeService.getActiveRoutes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable Long id) {
        return routeService.getRouteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Route> getRoutesBetweenCities(
            @RequestParam Long startCityId,
            @RequestParam Long endCityId) {
        return routeService.getRoutesBetweenCities(startCityId, endCityId);
    }

    @GetMapping("/transport-type/{transportTypeId}")
    public List<Route> getRoutesByTransportType(@PathVariable Long transportTypeId) {
        return routeService.getRoutesByTransportType(transportTypeId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Route createRoute(@RequestBody Route route) {
        return routeService.createRoute(route);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<Route> updateRoute(@PathVariable Long id, @RequestBody Route routeDetails) {
        Route updatedRoute = routeService.updateRoute(id, routeDetails);
        return updatedRoute != null ? ResponseEntity.ok(updatedRoute) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<Route> activateRoute(@PathVariable Long id) {
        Route route = routeService.activateRoute(id);
        return route != null ? ResponseEntity.ok(route) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<Route> deactivateRoute(@PathVariable Long id) {
        Route route = routeService.deactivateRoute(id);
        return route != null ? ResponseEntity.ok(route) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.ok().build();
    }
}
