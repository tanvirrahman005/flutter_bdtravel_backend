package com.tanvir.TicketingSystem.repository;

import com.tanvir.TicketingSystem.entity.Booking;
import com.tanvir.TicketingSystem.entity.BookingSeat;
import com.tanvir.TicketingSystem.entity.SeatLayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
    List<BookingSeat> findByBooking(Booking booking);

    List<BookingSeat> findByBookingId(Long bookingId);

    List<BookingSeat> findBySeatLayout(SeatLayout seatLayout);

    @Query("SELECT bs FROM BookingSeat bs WHERE bs.booking.schedule.id = :scheduleId")
    List<BookingSeat> findByScheduleId(@Param("scheduleId") Long scheduleId);

    @Query("SELECT bs.seatLayout FROM BookingSeat bs WHERE bs.booking.schedule.id = :scheduleId AND bs.booking.bookingStatus = 'CONFIRMED'")
    List<SeatLayout> findBookedSeatsByScheduleId(@Param("scheduleId") Long scheduleId);

    boolean existsBySeatLayoutAndBookingBookingStatusIn(SeatLayout seatLayout, List<Booking.BookingStatus> statuses);
}
