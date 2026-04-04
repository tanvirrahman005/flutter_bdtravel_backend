package com.tanvir.TicketingSystem.controller;

import com.tanvir.TicketingSystem.dto.DashboardStats;
import com.tanvir.TicketingSystem.repository.BookingRepository;
import com.tanvir.TicketingSystem.repository.RouteRepository;
import com.tanvir.TicketingSystem.repository.UserRepository;
import com.tanvir.TicketingSystem.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        stats.setTotalBookings(bookingRepository.count());
        stats.setActiveRoutes(routeRepository.countActiveRoutes());
        stats.setTotalUsers(userRepository.count());
        stats.setFleetSize(vehicleRepository.countActiveVehicles());

        Double totalRevenue = bookingRepository.sumTotalAmount();
        stats.setTotalRevenue(totalRevenue != null ? totalRevenue : 0.0);

        java.util.List<Object[]> monthlyData = bookingRepository.getMonthlyRevenue();
        java.util.Map<String, Double> monthlyMap = new java.util.LinkedHashMap<>();
        for (Object[] row : monthlyData) {
            String month = (String) row[0];
            Double revenue = ((java.math.BigDecimal) row[1]).doubleValue();
            monthlyMap.put(month, revenue);
        }
        stats.setMonthlyRevenue(monthlyMap);

        return ResponseEntity.ok(stats);
    }
}
