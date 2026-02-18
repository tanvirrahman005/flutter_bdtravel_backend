package com.tanvir.TicketingSystem.dto;

import java.math.BigDecimal;
import java.util.List;

public class CreateBookingRequest {
    private Long scheduleId;
    private Long userId;
    private String passengerName;
    private String passengerPhone;
    private String passengerEmail;
    private String passengerNid;
    private BigDecimal totalAmount;
    private List<SelectedSeat> selectedSeats;

    public static class SelectedSeat {
        private Long seatLayoutId;
        private BigDecimal seatPrice;

        public Long getSeatLayoutId() {
            return seatLayoutId;
        }

        public void setSeatLayoutId(Long seatLayoutId) {
            this.seatLayoutId = seatLayoutId;
        }

        public BigDecimal getSeatPrice() {
            return seatPrice;
        }

        public void setSeatPrice(BigDecimal seatPrice) {
            this.seatPrice = seatPrice;
        }
    }

    // Getters and Setters
    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerPhone() {
        return passengerPhone;
    }

    public void setPassengerPhone(String passengerPhone) {
        this.passengerPhone = passengerPhone;
    }

    public String getPassengerEmail() {
        return passengerEmail;
    }

    public void setPassengerEmail(String passengerEmail) {
        this.passengerEmail = passengerEmail;
    }

    public String getPassengerNid() {
        return passengerNid;
    }

    public void setPassengerNid(String passengerNid) {
        this.passengerNid = passengerNid;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<SelectedSeat> getSelectedSeats() {
        return selectedSeats;
    }

    public void setSelectedSeats(List<SelectedSeat> selectedSeats) {
        this.selectedSeats = selectedSeats;
    }
}
