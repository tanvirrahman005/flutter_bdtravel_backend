package com.tanvir.TicketingSystem.service;

import com.tanvir.TicketingSystem.entity.TransportType;
import com.tanvir.TicketingSystem.repository.TransportTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransportTypeService {

    @Autowired
    private TransportTypeRepository transportTypeRepository;

    public List<TransportType> getAllTransportTypes() {
        return transportTypeRepository.findAll();
    }

    public Optional<TransportType> getTransportTypeById(Long id) {
        return transportTypeRepository.findById(id);
    }

    public Optional<TransportType> getTransportTypeByName(String name) {
        return transportTypeRepository.findByName(name);
    }

    public TransportType createTransportType(TransportType transportType) {
        return transportTypeRepository.save(transportType);
    }

    public TransportType updateTransportType(Long id, TransportType transportTypeDetails) {
        Optional<TransportType> optionalTransportType = transportTypeRepository.findById(id);
        if (optionalTransportType.isPresent()) {
            TransportType transportType = optionalTransportType.get();
            transportType.setName(transportTypeDetails.getName());
            transportType.setDescription(transportTypeDetails.getDescription());
            return transportTypeRepository.save(transportType);
        }
        return null;
    }

    public void deleteTransportType(Long id) {
        transportTypeRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return transportTypeRepository.existsByName(name);
    }
}
