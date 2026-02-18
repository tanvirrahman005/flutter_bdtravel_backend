package com.tanvir.TicketingSystem.controller;

import com.tanvir.TicketingSystem.entity.TransportCompany;
import com.tanvir.TicketingSystem.service.TransportCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "*")
public class TransportCompanyController {

    @Autowired
    private TransportCompanyService transportCompanyService;

    @GetMapping
    public List<TransportCompany> getAllCompanies() {
        return transportCompanyService.getAllCompanies();
    }

    @GetMapping("/active")
    public List<TransportCompany> getActiveCompanies() {
        return transportCompanyService.getActiveCompanies();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransportCompany> getCompanyById(@PathVariable Long id) {
        return transportCompanyService.getCompanyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<TransportCompany> getCompanyByCode(@PathVariable String code) {
        return transportCompanyService.getCompanyByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public TransportCompany createCompany(@RequestBody TransportCompany company) {
        return transportCompanyService.createCompany(company);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransportCompany> updateCompany(@PathVariable Long id,
            @RequestBody TransportCompany companyDetails) {
        TransportCompany updatedCompany = transportCompanyService.updateCompany(id, companyDetails);
        return updatedCompany != null ? ResponseEntity.ok(updatedCompany) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransportCompany> activateCompany(@PathVariable Long id) {
        TransportCompany company = transportCompanyService.activateCompany(id);
        return company != null ? ResponseEntity.ok(company) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransportCompany> deactivateCompany(@PathVariable Long id) {
        TransportCompany company = transportCompanyService.deactivateCompany(id);
        return company != null ? ResponseEntity.ok(company) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        transportCompanyService.deleteCompany(id);
        return ResponseEntity.ok().build();
    }
}
