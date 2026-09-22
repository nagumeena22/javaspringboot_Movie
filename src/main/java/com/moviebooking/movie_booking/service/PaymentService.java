package com.moviebooking.movie_booking.service;

import com.moviebooking.movie_booking.Entity.Booking;
import com.moviebooking.movie_booking.Entity.Payment;
import com.moviebooking.movie_booking.Entity.PaymentStatus;
import com.moviebooking.movie_booking.Exception.ResourceNotFoundException;
import com.moviebooking.movie_booking.Repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Mock payment gateway for the college-project version of this app.
 * Swap processPayment()'s body for a real gateway call (Razorpay/Stripe/etc.)
 * later without touching BookingService's contract.
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    // 0.0 - 1.0. Set to 1.0 while demoing so payments always succeed;
    // lower it (e.g. 0.85) to also exercise the failure/rollback path.
    @Value("${app.mock-payment.success-rate:0.9}")
    private double successRate;

    @Transactional
    public Payment processPayment(Booking booking, String paymentMethod) {
        boolean success = ThreadLocalRandom.current().nextDouble() < successRate;

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getTotalAmount())
                .paymentMethod(paymentMethod)
                .transactionId(generateTransactionId())
                .status(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .paymentDate(LocalDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public Payment getByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("No payment found for booking " + bookingId));
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
