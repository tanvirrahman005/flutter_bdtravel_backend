package com.tanvir.TicketingSystem.scheduler;

import com.tanvir.TicketingSystem.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class BookingScheduler {

    private static final Logger logger = Logger.getLogger(BookingScheduler.class.getName());

    @Autowired
    private BookingService bookingService;

    // Run every minute
    @Scheduled(fixedRate = 60000)
    public void expirePendingBookings() {
        // Expire bookings older than 30 minutes
        int expiredCount = bookingService.expirePendingBookings(30);
        if (expiredCount > 0) {
            logger.info("Expired " + expiredCount + " pending bookings.");
        }
    }
}
