package com.tanvir.TicketingSystem.controller;

import com.tanvir.TicketingSystem.entity.BookingSeat;
import com.tanvir.TicketingSystem.entity.SeatLayout;
import com.tanvir.TicketingSystem.service.BookingSeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/booking-seats")
@CrossOrigin(origins = "*")
public class BookingSeatController {

    @Autowired
    private BookingSeatService bookingSeatService;

    @GetMapping
    public List<BookingSeat> getAllBookingSeats() {
        return bookingSeatService.getAllBookingSeats();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingSeat> getBookingSeatById(@PathVariable Long id) {
        return bookingSeatService.getBookingSeatById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/booking/{bookingId}")
    public List<BookingSeat> getSeatsByBooking(@PathVariable Long bookingId) {
        return bookingSeatService.getSeatsByBooking(bookingId);
    }

    @GetMapping("/schedule/{scheduleId}/booked")
    public List<SeatLayout> getBookedSeatsBySchedule(@PathVariable Long scheduleId) {
        return bookingSeatService.getBookedSeatsBySchedule(scheduleId);
    }

    @PostMapping
    public BookingSeat createBookingSeat(@RequestBody BookingSeat bookingSeat) {
        return bookingSeatService.createBookingSeat(bookingSeat);
    }

    @PostMapping("/bulk")
    public List<BookingSeat> createMultipleBookingSeats(@RequestBody List<BookingSeat> bookingSeats) {
        return bookingSeatService.createMultipleBookingSeats(bookingSeats);
    }

    @GetMapping("/seat/{seatLayoutId}/available")
    public ResponseEntity<Boolean> isSeatAvailable(@PathVariable Long seatLayoutId) {
        boolean isAvailable = bookingSeatService.isSeatAvailableForBooking(seatLayoutId);
        return ResponseEntity.ok(isAvailable);
    }

    @PostMapping("/calculate-price")
    public BigDecimal calculateTotalSeatPrice(
            @RequestParam List<Long> seatLayoutIds,
            @RequestParam BigDecimal basePrice) {
        return bookingSeatService.calculateTotalSeatPrice(seatLayoutIds, basePrice);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingSeat> updateBookingSeat(@PathVariable Long id, @RequestBody BookingSeat seatDetails) {
        BookingSeat updatedSeat = bookingSeatService.updateBookingSeat(id, seatDetails);
        return updatedSeat != null ? ResponseEntity.ok(updatedSeat) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteBookingSeat(@PathVariable Long id) {
        bookingSeatService.deleteBookingSeat(id);
        return ResponseEntity.ok().build();
    }
}
