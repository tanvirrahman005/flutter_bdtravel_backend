package com.tanvir.TicketingSystem.dto;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private String userName;
    private String email;
    private String currentPassword;
}
