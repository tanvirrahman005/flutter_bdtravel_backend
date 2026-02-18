package com.tanvir.TicketingSystem.service;

import com.tanvir.TicketingSystem.entity.TransportCompany;
import com.tanvir.TicketingSystem.repository.TransportCompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransportCompanyService {

    @Autowired
    private TransportCompanyRepository transportCompanyRepository;

    public List<TransportCompany> getAllCompanies() {
        return transportCompanyRepository.findAll();
    }

    public List<TransportCompany> getActiveCompanies() {
        return transportCompanyRepository.findByIsActiveTrue();
    }

    public Optional<TransportCompany> getCompanyById(Long id) {
        return transportCompanyRepository.findById(id);
    }

    public Optional<TransportCompany> getCompanyByCode(String code) {
        return transportCompanyRepository.findByCode(code);
    }

    public TransportCompany createCompany(TransportCompany company) {
        return transportCompanyRepository.save(company);
    }

    public TransportCompany updateCompany(Long id, TransportCompany companyDetails) {
        Optional<TransportCompany> optionalCompany = transportCompanyRepository.findById(id);
        if (optionalCompany.isPresent()) {
            TransportCompany company = optionalCompany.get();
            company.setName(companyDetails.getName());
            company.setContactNumber(companyDetails.getContactNumber());
            company.setEmail(companyDetails.getEmail());
            company.setAddress(companyDetails.getAddress());
            company.setLogoUrl(companyDetails.getLogoUrl());
            company.setIsActive(companyDetails.getIsActive());
            return transportCompanyRepository.save(company);
        }
        return null;
    }

    public TransportCompany activateCompany(Long id) {
        Optional<TransportCompany> optionalCompany = transportCompanyRepository.findById(id);
        if (optionalCompany.isPresent()) {
            TransportCompany company = optionalCompany.get();
            company.setIsActive(true);
            return transportCompanyRepository.save(company);
        }
        return null;
    }

    public TransportCompany deactivateCompany(Long id) {
        Optional<TransportCompany> optionalCompany = transportCompanyRepository.findById(id);
        if (optionalCompany.isPresent()) {
            TransportCompany company = optionalCompany.get();
            company.setIsActive(false);
            return transportCompanyRepository.save(company);
        }
        return null;
    }

    public void deleteCompany(Long id) {
        transportCompanyRepository.deleteById(id);
    }

    public boolean existsByCode(String code) {
        return transportCompanyRepository.existsByCode(code);
    }
}
