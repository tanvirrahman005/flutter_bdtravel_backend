package com.tanvir.TicketingSystem.service;

import com.tanvir.TicketingSystem.entity.Schedule;
import com.tanvir.TicketingSystem.entity.Schedule.ScheduleStatus;
import com.tanvir.TicketingSystem.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private com.tanvir.TicketingSystem.repository.BookingRepository bookingRepository;

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Optional<Schedule> getScheduleById(Long id) {
        return scheduleRepository.findById(id);
    }

    public List<Schedule> getSchedulesByRoute(Long routeId) {
        return scheduleRepository.findByRoute(routeService.getRouteById(routeId).orElse(null));
    }

    public List<Schedule> getUpcomingSchedulesByRoute(Long routeId) {
        return scheduleRepository.findUpcomingSchedulesByRoute(routeId, LocalDateTime.now());
    }

    public List<Schedule> getAvailableSchedules(Long startCityId, Long endCityId, LocalDateTime departureDate,
            Long transportTypeId) {
        return scheduleRepository.findAvailableSchedules(startCityId, endCityId, departureDate, transportTypeId);
    }

    public List<Schedule> getSchedulesByTransportTypeAndDate(Long transportTypeId, LocalDateTime startDate) {
        return scheduleRepository.findSchedulesByTransportTypeAndDate(transportTypeId, startDate);
    }

    public Schedule createSchedule(Schedule schedule) {
        if (schedule.getAvailableSeats() == null && schedule.getVehicle() != null) {
            schedule.setAvailableSeats(schedule.getVehicle().getTotalSeats());
        }
        return scheduleRepository.save(schedule);
    }

    public Schedule updateSchedule(Long id, Schedule scheduleDetails) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(id);
        if (optionalSchedule.isPresent()) {
            Schedule schedule = optionalSchedule.get();
            schedule.setVehicle(scheduleDetails.getVehicle());
            schedule.setRoute(scheduleDetails.getRoute());
            schedule.setDepartureTime(scheduleDetails.getDepartureTime());
            schedule.setArrivalTime(scheduleDetails.getArrivalTime());
            schedule.setBasePrice(scheduleDetails.getBasePrice());
            schedule.setAvailableSeats(scheduleDetails.getAvailableSeats());
            schedule.setStatus(scheduleDetails.getStatus());
            return scheduleRepository.save(schedule);
        }
        return null;
    }

    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    public void updateScheduleStatus(Long id, ScheduleStatus status) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(id);
        if (optionalSchedule.isPresent()) {
            Schedule schedule = optionalSchedule.get();
            schedule.setStatus(status);
            scheduleRepository.save(schedule);
        }
    }

    public void decreaseAvailableSeats(Long scheduleId, int seats) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(scheduleId);
        if (optionalSchedule.isPresent()) {
            Schedule schedule = optionalSchedule.get();
            schedule.setAvailableSeats(schedule.getAvailableSeats() - seats);
            scheduleRepository.save(schedule);
        }
    }

    public void increaseAvailableSeats(Long scheduleId, int seats) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(scheduleId);
        if (optionalSchedule.isPresent()) {
            Schedule schedule = optionalSchedule.get();
            schedule.setAvailableSeats(schedule.getAvailableSeats() + seats);
            scheduleRepository.save(schedule);
        }
    }

    public void syncAvailableSeats(Long scheduleId) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(scheduleId);
        if (optionalSchedule.isPresent()) {
            Schedule schedule = optionalSchedule.get();
            int totalSeats = schedule.getVehicle().getTotalSeats();
            Long confirmedBookings = bookingRepository.countConfirmedBookingsByScheduleId(scheduleId);
            schedule.setAvailableSeats(totalSeats - confirmedBookings.intValue());
            scheduleRepository.save(schedule);
        }
    }
}
