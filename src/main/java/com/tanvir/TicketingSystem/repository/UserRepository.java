package com.tanvir.TicketingSystem.repository;

import com.tanvir.TicketingSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUserName(String userName);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUserNameOrEmail(String userName, String email);
    
    boolean existsByUserName(String userName);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(User.UserRole role);
    
    List<User> findByIsActive(Boolean isActive);
    
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.password = :password")
    Optional<User> findByEmailAndPassword(@Param("email") String email, @Param("password") String password);
    
    @Query("SELECT u FROM User u WHERE u.userName = :userName AND u.password = :password")
    Optional<User> findByUserNameAndPassword(@Param("userName") String userName, @Param("password") String password);
    
    @Query("SELECT u FROM User u WHERE (u.userName = :login OR u.email = :login) AND u.password = :password AND u.isActive = true")
    Optional<User> authenticate(@Param("login") String login, @Param("password") String password);
}
