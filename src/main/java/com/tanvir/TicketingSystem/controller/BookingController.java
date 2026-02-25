package com.tanvir.TicketingSystem.controller;

import com.tanvir.TicketingSystem.entity.Booking;
import com.tanvir.TicketingSystem.dto.CreateBookingRequest;
import com.tanvir.TicketingSystem.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<Booking> getBookingByReference(@PathVariable String reference) {
        return bookingService.getBookingByReference(reference)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/phone/{phone}")
    public List<Booking> getBookingsByPhone(@PathVariable String phone) {
        return bookingService.getBookingsByPhone(phone);
    }

    @GetMapping("/phone/{phone}/active")
    public List<Booking> getActiveBookingsByPhone(@PathVariable String phone) {
        return bookingService.getActiveBookingsByPhone(phone);
    }

    @GetMapping("/user/{userId}")
    public List<Booking> getBookingsByUserId(@PathVariable Long userId) {
        return bookingService.getBookingsByUserId(userId);
    }

    @GetMapping("/date-range")
    public List<Booking> getBookingsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return bookingService.getBookingsByDateRange(startDate, endDate);
    }

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
        return bookingService.createBooking(booking);
    }

    // New endpoint for seat-selection component
    @PostMapping("/create-with-seats")
    public ResponseEntity<Object> createBookingWithSeats(@RequestBody CreateBookingRequest request) {
        try {
            Booking booking = bookingService.createBookingWithSeats(request);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Booking> updateBookingStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Booking.BookingStatus bookingStatus = Booking.BookingStatus.valueOf(status.toUpperCase());
            Booking updatedBooking = bookingService.updateBookingStatus(id, bookingStatus);
            return updatedBooking != null ? ResponseEntity.ok(updatedBooking) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody Booking booking) {
        Booking updatedBooking = bookingService.updateBooking(id, booking);
        return updatedBooking != null ? ResponseEntity.ok(updatedBooking) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/schedule/{scheduleId}/confirmed-count")
    public Long getConfirmedBookingsCount(@PathVariable Long scheduleId) {
        return bookingService.countConfirmedBookingsBySchedule(scheduleId);
    }

    @GetMapping("/schedule/{scheduleId}/booked-seats")
    public List<com.tanvir.TicketingSystem.entity.SeatLayout> getBookedSeats(@PathVariable Long scheduleId) {
        return bookingService.getBookedSeatsBySchedule(scheduleId);
    }

    @Autowired
    private com.tanvir.TicketingSystem.service.ReportService reportService;

    @GetMapping("/{id}/ticket")
    public org.springframework.http.ResponseEntity<org.springframework.core.io.InputStreamResource> downloadTicket(
            @PathVariable Long id) {
        return bookingService.getBookingById(id)
                .map(booking -> {
                    java.io.ByteArrayInputStream bis = reportService.generateTicketPdf(booking);
                    org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                    headers.add("Content-Disposition",
                            "attachment; filename=ticket-" + booking.getBookingReference() + ".pdf");

                    return org.springframework.http.ResponseEntity
                            .ok()
                            .headers(headers)
                            .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                            .body(new org.springframework.core.io.InputStreamResource(bis));
                })
                .orElse(org.springframework.http.ResponseEntity.notFound().build());
    }
}
