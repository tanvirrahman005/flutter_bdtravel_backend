package com.tanvir.TicketingSystem.repository;

import com.tanvir.TicketingSystem.entity.TransportCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransportCompanyRepository extends JpaRepository<TransportCompany, Long> {
    Optional<TransportCompany> findByCode(String code);
    List<TransportCompany> findByIsActiveTrue();
    boolean existsByCode(String code);
    boolean existsByName(String name);
}
