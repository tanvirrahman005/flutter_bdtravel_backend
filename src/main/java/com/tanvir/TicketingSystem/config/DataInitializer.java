package com.tanvir.TicketingSystem.config;

import com.tanvir.TicketingSystem.entity.User;
import com.tanvir.TicketingSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin user exists
        List<User> admins = userRepository.findByRole(User.UserRole.ADMIN);

        if (admins.isEmpty()) {
            User admin = new User();
            admin.setUserName("admin");
            admin.setEmail("admin@ticketbd.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.UserRole.ADMIN);
            admin.setIsActive(true);
            admin.setRememberMe(false);

            userRepository.save(admin);
            System.out.println("Default admin user created: admin / admin123");
        }
    }
}
