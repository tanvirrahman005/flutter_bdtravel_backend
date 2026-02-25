package com.tanvir.TicketingSystem.controller;

import com.tanvir.TicketingSystem.entity.Schedule;
import com.tanvir.TicketingSystem.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@CrossOrigin(origins = "*")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping
    public List<Schedule> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable Long id) {
        return scheduleService.getScheduleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/route/{routeId}")
    public List<Schedule> getSchedulesByRoute(@PathVariable Long routeId) {
        return scheduleService.getSchedulesByRoute(routeId);
    }

    @GetMapping("/route/{routeId}/upcoming")
    public List<Schedule> getUpcomingSchedulesByRoute(@PathVariable Long routeId) {
        return scheduleService.getUpcomingSchedulesByRoute(routeId);
    }

    @GetMapping("/search")
    public List<Schedule> getAvailableSchedules(
            @RequestParam Long startCityId,
            @RequestParam Long endCityId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDate,
            @RequestParam(required = false) Long transportTypeId) {
        return scheduleService.getAvailableSchedules(startCityId, endCityId, departureDate, transportTypeId);
    }

    @GetMapping("/transport-type/{transportTypeId}")
    public List<Schedule> getSchedulesByTransportType(
            @PathVariable Long transportTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate) {
        return scheduleService.getSchedulesByTransportTypeAndDate(transportTypeId, startDate);
    }

    @PostMapping
    public Schedule createSchedule(@RequestBody Schedule schedule) {
        return scheduleService.createSchedule(schedule);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(@PathVariable Long id, @RequestBody Schedule scheduleDetails) {
        Schedule updatedSchedule = scheduleService.updateSchedule(id, scheduleDetails);
        return updatedSchedule != null ? ResponseEntity.ok(updatedSchedule) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateScheduleStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Schedule.ScheduleStatus scheduleStatus = Schedule.ScheduleStatus.valueOf(status.toUpperCase());
            scheduleService.updateScheduleStatus(id, scheduleStatus);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/sync-seats")
    public ResponseEntity<Void> syncAvailableSeats(@PathVariable Long id) {
        scheduleService.syncAvailableSeats(id);
        return ResponseEntity.ok().build();
    }
}
