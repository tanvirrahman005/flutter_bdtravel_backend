package com.tanvir.TicketingSystem.service;

import com.tanvir.TicketingSystem.entity.Booking;
import com.tanvir.TicketingSystem.entity.Schedule;
import com.tanvir.TicketingSystem.entity.BookingSeat;
import com.tanvir.TicketingSystem.entity.SeatLayout;
import com.tanvir.TicketingSystem.entity.User;
import com.tanvir.TicketingSystem.dto.CreateBookingRequest;
import com.tanvir.TicketingSystem.repository.BookingRepository;
import com.tanvir.TicketingSystem.repository.BookingSeatRepository;
import com.tanvir.TicketingSystem.repository.SeatLayoutRepository;
import com.tanvir.TicketingSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private BookingSeatRepository bookingSeatRepository;

    @Autowired
    private SeatLayoutRepository seatLayoutRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public Optional<Booking> getBookingByReference(String bookingReference) {
        return bookingRepository.findByBookingReference(bookingReference);
    }

    public List<Booking> getBookingsByPhone(String passengerPhone) {
        return bookingRepository.findByPassengerPhone(passengerPhone);
    }

    public List<Booking> getActiveBookingsByPhone(String passengerPhone) {
        return bookingRepository.findActiveBookingsByPhone(passengerPhone);
    }

    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getBookingsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepository.findBookingsByDateRange(startDate, endDate);
    }

    public Booking createBooking(Booking booking) {
        // Generate unique booking reference
        String bookingReference = generateBookingReference();
        booking.setBookingReference(bookingReference);

        // Set booking date
        booking.setBookingDate(new java.sql.Timestamp(System.currentTimeMillis()));

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking createBookingWithSeats(CreateBookingRequest request) {
        // Get the schedule
        Schedule schedule = scheduleService.getScheduleById(request.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + request.getScheduleId()));

        // Create the booking
        Booking booking = new Booking();
        booking.setBookingReference(generateBookingReference());
        booking.setSchedule(schedule);
        booking.setPassengerName(request.getPassengerName());
        booking.setPassengerPhone(request.getPassengerPhone());
        booking.setPassengerEmail(request.getPassengerEmail());
        booking.setPassengerNid(request.getPassengerNid());
        booking.setTotalAmount(request.getTotalAmount());
        booking.setBookingStatus(Booking.BookingStatus.PENDING);
        booking.setBookingDate(new java.sql.Timestamp(System.currentTimeMillis()));

        // Set user if userId is provided
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId()).orElse(null);
            booking.setUser(user);
        }

        // Save the booking first
        Booking savedBooking = bookingRepository.save(booking);

        // Create booking seats
        if (request.getSelectedSeats() != null) {
            for (CreateBookingRequest.SelectedSeat selectedSeat : request.getSelectedSeats()) {
                SeatLayout seatLayout = seatLayoutRepository.findById(selectedSeat.getSeatLayoutId())
                        .orElseThrow(() -> new RuntimeException(
                                "Seat not found with id: " + selectedSeat.getSeatLayoutId()));

                // Mark seat as unavailable
                seatLayout.setIsAvailable(false);
                seatLayoutRepository.save(seatLayout);

                // Create BookingSeat entry
                BookingSeat bookingSeat = new BookingSeat();
                bookingSeat.setBooking(savedBooking);
                bookingSeat.setSeatLayout(seatLayout);
                bookingSeat.setSeatPrice(selectedSeat.getSeatPrice());
                bookingSeatRepository.save(bookingSeat);
            }
        }

        // Update available seats in schedule
        int seatsBooked = request.getSelectedSeats() != null ? request.getSelectedSeats().size() : 0;
        scheduleService.decreaseAvailableSeats(schedule.getId(), seatsBooked);

        return savedBooking;
    }

    public Booking updateBookingStatus(Long id, Booking.BookingStatus status) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setBookingStatus(status);
            return bookingRepository.save(booking);
        }
        return null;
    }

    public Booking updateBooking(Long id, Booking bookingDetails) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setPassengerName(bookingDetails.getPassengerName());
            booking.setPassengerPhone(bookingDetails.getPassengerPhone());
            booking.setPassengerEmail(bookingDetails.getPassengerEmail());
            booking.setPassengerNid(bookingDetails.getPassengerNid());
            booking.setTotalAmount(bookingDetails.getTotalAmount());
            booking.setBookingStatus(bookingDetails.getBookingStatus());
            if (bookingDetails.getSchedule() != null) {
                booking.setSchedule(bookingDetails.getSchedule());
            }
            return bookingRepository.save(booking);
        }
        return null;
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    @Transactional
    public void cancelBooking(Long id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();

            // Release the booked seats
            releaseBookingSeats(booking);

            // Update booking status to cancelled
            booking.setBookingStatus(Booking.BookingStatus.CANCELLED);
            bookingRepository.save(booking);
        }
    }

    /**
     * Release all seats associated with a booking (mark them as available)
     */
    @Transactional
    public void releaseBookingSeats(Booking booking) {
        // Find all booking seats for this booking
        List<BookingSeat> bookingSeats = bookingSeatRepository.findByBookingId(booking.getId());

        for (BookingSeat bookingSeat : bookingSeats) {
            // Mark the seat as available again
            SeatLayout seatLayout = bookingSeat.getSeatLayout();
            if (seatLayout != null) {
                seatLayout.setIsAvailable(true);
                seatLayoutRepository.save(seatLayout);
            }
        }

        // Update available seats count in schedule
        int seatsReleased = bookingSeats.size();
        if (seatsReleased > 0 && booking.getSchedule() != null) {
            scheduleService.increaseAvailableSeats(booking.getSchedule().getId(), seatsReleased);
        }
    }

    /**
     * Find and expire pending bookings older than specified minutes
     */
    @Transactional
    public int expirePendingBookings(int expiryMinutes) {
        // Calculate the cutoff time
        java.sql.Timestamp cutoffTime = new java.sql.Timestamp(
                System.currentTimeMillis() - (expiryMinutes * 60 * 1000L));

        // Find all pending bookings created before cutoff time
        List<Booking> pendingBookings = bookingRepository.findPendingBookingsCreatedBefore(cutoffTime);

        int expiredCount = 0;
        for (Booking booking : pendingBookings) {
            // Release the seats
            releaseBookingSeats(booking);

            // Mark as cancelled (expired)
            booking.setBookingStatus(Booking.BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            expiredCount++;
        }

        return expiredCount;
    }

    public Long countConfirmedBookingsBySchedule(Long scheduleId) {
        return bookingRepository.countConfirmedBookingsByScheduleId(scheduleId);
    }

    public List<SeatLayout> getBookedSeatsBySchedule(Long scheduleId) {
        return bookingSeatRepository.findBookedSeatsByScheduleId(scheduleId);
    }

    private String generateBookingReference() {
        return "BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public boolean existsByBookingReference(String bookingReference) {
        return bookingRepository.existsByBookingReference(bookingReference);
    }
}
