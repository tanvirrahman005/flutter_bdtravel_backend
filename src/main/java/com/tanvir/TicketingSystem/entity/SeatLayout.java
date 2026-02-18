package com.tanvir.TicketingSystem.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.sql.Timestamp;

@Entity
@Table(name = "seat_layout")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SeatLayout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Vehicle vehicle;

    @Column(name = "seat_number", nullable = false, length = 10)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type")
    private SeatType seatType = SeatType.REGULAR;

    @Enumerated(EnumType.STRING)
    @Column(name = "deck_level")
    private DeckLevel deckLevel = DeckLevel.LOWER;

    @Column(name = "row_position")
    private Integer rowPosition;

    @Column(name = "column_position")
    private Integer columnPosition;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    public enum SeatType {
        REGULAR, PREMIUM, BUSINESS
    }

    public enum DeckLevel {
        LOWER, UPPER
    }

    // Constructors
    public SeatLayout() {}

    public SeatLayout(Vehicle vehicle, String seatNumber, SeatType seatType) {
        this.vehicle = vehicle;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public SeatType getSeatType() { return seatType; }
    public void setSeatType(SeatType seatType) { this.seatType = seatType; }
    public DeckLevel getDeckLevel() { return deckLevel; }
    public void setDeckLevel(DeckLevel deckLevel) { this.deckLevel = deckLevel; }
    public Integer getRowPosition() { return rowPosition; }
    public void setRowPosition(Integer rowPosition) { this.rowPosition = rowPosition; }
    public Integer getColumnPosition() { return columnPosition; }
    public void setColumnPosition(Integer columnPosition) { this.columnPosition = columnPosition; }
    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
