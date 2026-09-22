package com.moviebooking.movie_booking.controller;

import com.moviebooking.movie_booking.Dto.PaymentResponse;
import com.moviebooking.movie_booking.Entity.Payment;
import com.moviebooking.movie_booking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentResponse> getPaymentForBooking(@PathVariable Long bookingId) {
        Payment p = paymentService.getByBookingId(bookingId);
        return ResponseEntity.ok(PaymentResponse.builder()
                .paymentId(p.getId())
                .amount(p.getAmount())
                .paymentMethod(p.getPaymentMethod())
                .transactionId(p.getTransactionId())
                .status(p.getStatus())
                .paymentDate(p.getPaymentDate())
                .build());
    }
}
