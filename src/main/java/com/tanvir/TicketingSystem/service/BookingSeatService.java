package com.tanvir.TicketingSystem.service;

import com.tanvir.TicketingSystem.entity.Booking;
import com.tanvir.TicketingSystem.entity.BookingSeat;
import com.tanvir.TicketingSystem.entity.SeatLayout;
import com.tanvir.TicketingSystem.repository.BookingSeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class BookingSeatService {

    @Autowired
    private BookingSeatRepository bookingSeatRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private SeatLayoutService seatLayoutService;

    public List<BookingSeat> getAllBookingSeats() {
        return bookingSeatRepository.findAll();
    }

    public Optional<BookingSeat> getBookingSeatById(Long id) {
        return bookingSeatRepository.findById(id);
    }

    public List<BookingSeat> getSeatsByBooking(Long bookingId) {
        Optional<Booking> booking = bookingService.getBookingById(bookingId);
        return booking.map(bookingSeatRepository::findByBooking).orElse(List.of());
    }

    public List<SeatLayout> getBookedSeatsBySchedule(Long scheduleId) {
        return bookingSeatRepository.findBookedSeatsByScheduleId(scheduleId);
    }

    public BookingSeat createBookingSeat(BookingSeat bookingSeat) {
        return bookingSeatRepository.save(bookingSeat);
    }

    public List<BookingSeat> createMultipleBookingSeats(List<BookingSeat> bookingSeats) {
        // Update seat availability
        for (BookingSeat bookingSeat : bookingSeats) {
            seatLayoutService.updateSeatAvailability(bookingSeat.getSeatLayout().getId(), false);
        }

        return bookingSeatRepository.saveAll(bookingSeats);
    }

    public boolean isSeatAvailableForBooking(Long seatLayoutId) {
        return seatLayoutService.isSeatAvailable(seatLayoutId);
    }

    public BigDecimal calculateTotalSeatPrice(List<Long> seatLayoutIds, BigDecimal basePrice) {
        // You can implement different pricing logic based on seat type
        int seatCount = seatLayoutIds.size();
        return basePrice.multiply(BigDecimal.valueOf(seatCount));
    }

    public BookingSeat updateBookingSeat(Long id, BookingSeat seatDetails) {
        Optional<BookingSeat> optionalSeat = bookingSeatRepository.findById(id);
        if (optionalSeat.isPresent()) {
            BookingSeat seat = optionalSeat.get();
            seat.setBooking(seatDetails.getBooking());
            seat.setSeatLayout(seatDetails.getSeatLayout());
            seat.setSeatPrice(seatDetails.getSeatPrice());
            return bookingSeatRepository.save(seat);
        }
        return null;
    }

    public void deleteBookingSeat(Long id) {
        bookingSeatRepository.deleteById(id);
    }
}
