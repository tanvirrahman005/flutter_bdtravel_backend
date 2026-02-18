package com.tanvir.TicketingSystem.controller;

import com.tanvir.TicketingSystem.entity.TransportType;
import com.tanvir.TicketingSystem.service.TransportTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transport-types")
@CrossOrigin(origins = "*")
public class TransportTypeController {

    @Autowired
    private TransportTypeService transportTypeService;

    @GetMapping
    public List<TransportType> getAllTransportTypes() {
        return transportTypeService.getAllTransportTypes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransportType> getTransportTypeById(@PathVariable Long id) {
        return transportTypeService.getTransportTypeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public TransportType createTransportType(@RequestBody TransportType transportType) {
        return transportTypeService.createTransportType(transportType);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransportType> updateTransportType(@PathVariable Long id,
            @RequestBody TransportType transportTypeDetails) {
        TransportType updatedTransportType = transportTypeService.updateTransportType(id, transportTypeDetails);
        return updatedTransportType != null ? ResponseEntity.ok(updatedTransportType)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTransportType(@PathVariable Long id) {
        transportTypeService.deleteTransportType(id);
        return ResponseEntity.ok().build();
    }
}
