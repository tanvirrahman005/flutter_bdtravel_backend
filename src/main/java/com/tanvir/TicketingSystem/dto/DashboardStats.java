package com.tanvir.TicketingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private long totalBookings;
    private long activeRoutes;
    private long totalUsers;
    private long fleetSize;
}
