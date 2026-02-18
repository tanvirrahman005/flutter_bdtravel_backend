package com.tanvir.TicketingSystem.repository;

import com.tanvir.TicketingSystem.entity.Booking;
import com.tanvir.TicketingSystem.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingReference(String bookingReference);

    List<Booking> findBySchedule(Schedule schedule);

    List<Booking> findByBookingStatus(Booking.BookingStatus status);

    List<Booking> findByPassengerPhone(String passengerPhone);

    List<Booking> findByPassengerEmail(String passengerEmail);

    @Query("SELECT b FROM Booking b WHERE b.bookingDate BETWEEN :startDate AND :endDate ORDER BY b.bookingDate DESC")
    List<Booking> findBookingsByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.schedule.id = :scheduleId AND b.bookingStatus = 'CONFIRMED'")
    Long countConfirmedBookingsByScheduleId(@Param("scheduleId") Long scheduleId);

    @Query("SELECT b FROM Booking b WHERE b.passengerPhone = :phone AND b.bookingStatus IN ('PENDING', 'CONFIRMED')")
    List<Booking> findActiveBookingsByPhone(@Param("phone") String phone);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId ORDER BY b.createdAt DESC")
    List<Booking> findByUserId(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.bookingStatus = 'PENDING' AND b.bookingDate < :cutoffTime")
    List<Booking> findPendingBookingsCreatedBefore(@Param("cutoffTime") java.sql.Timestamp cutoffTime);

    boolean existsByBookingReference(String bookingReference);
}
