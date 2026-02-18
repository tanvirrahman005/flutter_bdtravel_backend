package com.tanvir.TicketingSystem.controller;

import com.tanvir.TicketingSystem.entity.Booking;
import com.tanvir.TicketingSystem.entity.Payment;
import com.tanvir.TicketingSystem.entity.Route;
import com.tanvir.TicketingSystem.entity.Vehicle;
import com.tanvir.TicketingSystem.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private RouteService routeService;

    @GetMapping("/dashboard/stats")
    public Map<String, Object> getDashboardStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        Map<String, Object> stats = new HashMap<>();

        // Total revenue
        BigDecimal revenue = paymentService.getTotalRevenue(startDate, endDate);
        stats.put("totalRevenue", revenue);

        // Total bookings count (you might need to implement this method)
        List<Booking> bookings = bookingService.getBookingsByDateRange(startDate, endDate);
        stats.put("totalBookings", bookings.size());

        // Total successful payments
        List<Payment> payments = paymentService.getPaymentsByDateRange(startDate, endDate);
        long successfulPayments = payments.stream()
                .filter(p -> p.getPaymentStatus() == Payment.PaymentStatus.SUCCESS)
                .count();
        stats.put("successfulPayments", successfulPayments);

        // Active vehicles count
        List<Vehicle> activeVehicles = vehicleService.getActiveVehicles();
        stats.put("activeVehicles", activeVehicles.size());

        // Active routes count
        List<Route> activeRoutes = routeService.getActiveRoutes();
        stats.put("activeRoutes", activeRoutes.size());

        return stats;
    }

    @GetMapping("/reports/bookings")
    public List<Booking> getBookingReports(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return bookingService.getBookingsByDateRange(startDate, endDate);
    }

    @GetMapping("/reports/payments")
    public List<Payment> getPaymentReports(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return paymentService.getPaymentsByDateRange(startDate, endDate);
    }
}
