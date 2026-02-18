package com.tanvir.TicketingSystem.repository;

import com.tanvir.TicketingSystem.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findByName(String name);
    Optional<City> findByCode(String code);
    List<City> findByIsActiveTrue();
    boolean existsByName(String name);
    boolean existsByCode(String code);
}
