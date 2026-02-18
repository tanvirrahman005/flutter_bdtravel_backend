package com.tanvir.TicketingSystem.service;

import com.tanvir.TicketingSystem.entity.Booking;
import com.tanvir.TicketingSystem.entity.Payment;
import com.tanvir.TicketingSystem.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingService bookingService;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public Optional<Payment> getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId);
    }

    public List<Payment> getPaymentsByBooking(Long bookingId) {
        return paymentRepository.findPaymentsByBookingId(bookingId);
    }

    public List<Payment> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findPaymentsByDateRange(startDate, endDate);
    }

    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment processPayment(com.tanvir.TicketingSystem.dto.PaymentRequest request) {
        Optional<Booking> booking = bookingService.getBookingById(request.getBookingId());

        if (booking.isPresent()) {
            Payment payment = new Payment();
            payment.setBooking(booking.get());

            try {
                payment.setPaymentMethod(Payment.PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid payment method: " + request.getPaymentMethod());
            }

            payment.setAmount(request.getAmount());

            // For card/bank mock logic vs mobile money logic
            // User requested Bkash/Nagad handling with account/pin
            // We'll generate a transaction ID based on timestamp and mobile number hash if
            // not provided
            String trxId = "TRX-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 1000);

            payment.setTransactionId(trxId);
            payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
            payment.setPaymentDate(new java.sql.Timestamp(System.currentTimeMillis()));

            // Save payment
            Payment savedPayment = paymentRepository.save(payment);

            // Update booking status to confirmed immediately
            bookingService.updateBookingStatus(request.getBookingId(), Booking.BookingStatus.CONFIRMED);

            return savedPayment;
        }
        throw new RuntimeException("Booking not found with id: " + request.getBookingId());
    }

    public void updatePaymentStatus(Long paymentId, Payment.PaymentStatus status) {
        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            payment.setPaymentStatus(status);
            if (status == Payment.PaymentStatus.SUCCESS) {
                payment.setPaymentDate(new java.sql.Timestamp(System.currentTimeMillis()));
            }
            paymentRepository.save(payment);
        }
    }

    public Payment updatePayment(Long id, Payment paymentDetails) {
        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            payment.setPaymentMethod(paymentDetails.getPaymentMethod());
            payment.setTransactionId(paymentDetails.getTransactionId());
            payment.setAmount(paymentDetails.getAmount());
            payment.setPaymentStatus(paymentDetails.getPaymentStatus());
            payment.setPaymentDate(paymentDetails.getPaymentDate());
            if (paymentDetails.getBooking() != null) {
                payment.setBooking(paymentDetails.getBooking());
            }
            return paymentRepository.save(payment);
        }
        return null;
    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    public BigDecimal getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal revenue = paymentRepository.getTotalRevenueByDateRange(startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    public boolean existsByTransactionId(String transactionId) {
        return paymentRepository.existsByTransactionId(transactionId);
    }
}
