package com.tanvir.TicketingSystem.service;

import com.tanvir.TicketingSystem.entity.City;
import com.tanvir.TicketingSystem.repository.CityRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {

    @Autowired
    private CityRepository cityRepository;

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    public List<City> getActiveCities() {
        return cityRepository.findByIsActiveTrue();
    }

    public Optional<City> getCityById(Long id) {
        return cityRepository.findById(id);
    }

    public Optional<City> getCityByName(String name) {
        return cityRepository.findByName(name);
    }

    public Optional<City> getCityByCode(String code) {
        return cityRepository.findByCode(code);
    }

    public City createCity(City city) {
        return cityRepository.save(city);
    }

    public City updateCity(Long id, City cityDetails) {
        Optional<City> optionalCity = cityRepository.findById(id);
        if (optionalCity.isPresent()) {
            City city = optionalCity.get();
            city.setName(cityDetails.getName());
            city.setBnName(cityDetails.getBnName());
            city.setCode(cityDetails.getCode());
            city.setIsActive(cityDetails.getIsActive());
            return cityRepository.save(city);
        }
        return null;
    }

    public City deactivateCity(Long id) {
        Optional<City> optionalCity = cityRepository.findById(id);
        if (optionalCity.isPresent()) {
            City city = optionalCity.get();
            city.setIsActive(false);
            return cityRepository.save(city);
        }
        return null;
    }

    public City activateCity(Long id) {
        Optional<City> optionalCity = cityRepository.findById(id);
        if (optionalCity.isPresent()) {
            City city = optionalCity.get();
            city.setIsActive(true);
            return cityRepository.save(city);
        }
        return null;
    }

    public void deleteCity(Long id) {
        cityRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return cityRepository.existsByName(name);
    }
}
