package com.tanvir.TicketingSystem.service;

import com.tanvir.TicketingSystem.entity.User;
import com.tanvir.TicketingSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }

    public List<User> getActiveUsers() {
        return userRepository.findByIsActive(true);
    }

    public User createUser(User user) {
        // Check if username already exists
        if (userRepository.existsByUserName(user.getUserName())) {
            throw new RuntimeException("Username already exists: " + user.getUserName());
        }
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }

        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Check if new username is taken by another user
            if (!user.getUserName().equals(userDetails.getUserName()) &&
                    userRepository.existsByUserName(userDetails.getUserName())) {
                throw new RuntimeException("Username already exists: " + userDetails.getUserName());
            }

            // Check if new email is taken by another user
            if (!user.getEmail().equals(userDetails.getEmail()) &&
                    userRepository.existsByEmail(userDetails.getEmail())) {
                throw new RuntimeException("Email already exists: " + userDetails.getEmail());
            }

            user.setUserName(userDetails.getUserName());
            user.setEmail(userDetails.getEmail());

            // Only update password if a new one is provided
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }

            user.setRole(userDetails.getRole());
            user.setRememberMe(userDetails.getRememberMe());
            user.setIsActive(userDetails.getIsActive());

            return userRepository.save(user);
        }
        return null;
    }

    public User updateProfile(Long id, com.tanvir.TicketingSystem.dto.ProfileUpdateRequest request) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Verify current password
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new RuntimeException("Incorrect password");
            }

            // Check if new username is taken by another user
            if (!user.getUserName().equals(request.getUserName()) &&
                    userRepository.existsByUserName(request.getUserName())) {
                throw new RuntimeException("Username already exists: " + request.getUserName());
            }

            // Check if new email is taken by another user
            if (!user.getEmail().equals(request.getEmail()) &&
                    userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists: " + request.getEmail());
            }

            user.setUserName(request.getUserName());
            user.setEmail(request.getEmail());

            return userRepository.save(user);
        }
        return null;
    }

    public User deactivateUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setIsActive(false);
            return userRepository.save(user);
        }
        return null;
    }

    public User activateUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setIsActive(true);
            return userRepository.save(user);
        }
        return null;
    }

    public Optional<User> authenticate(String login, String rawPassword) {
        // Try to find user by username or email
        Optional<User> userOptional = userRepository.findByUserNameOrEmail(login, login);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Check if user is active and password matches
            if (user.getIsActive() && passwordEncoder.matches(rawPassword, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public User updateRememberMe(Long id, Boolean rememberMe) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setRememberMe(rememberMe);
            return userRepository.save(user);
        }
        return null;
    }

    public boolean existsByUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Change password with verification
    public boolean changePassword(Long id, String currentPassword, String newPassword) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // Verify current password
            if (passwordEncoder.matches(currentPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    /**
     * Update user role (Admin only operation)
     */
    public User updateUserRole(Long id, User.UserRole newRole) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setRole(newRole);
            return userRepository.save(user);
        }
        return null;
    }

    /**
     * Delete user (returns boolean for success)
     */
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
