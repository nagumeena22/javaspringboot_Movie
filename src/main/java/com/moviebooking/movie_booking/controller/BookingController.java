package com.moviebooking.movie_booking.controller;

import com.moviebooking.movie_booking.Entity.User;
import com.moviebooking.movie_booking.Dto.BookingRequest;
import com.moviebooking.movie_booking.Dto.BookingResponse;
import com.moviebooking.movie_booking.Dto.LockSeatsRequest;
import com.moviebooking.movie_booking.Dto.LockSeatsResponse;
import com.moviebooking.movie_booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/seats/lock")
    public ResponseEntity<LockSeatsResponse> lockSeats(@Valid @RequestBody LockSeatsRequest request,
                                                         @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bookingService.lockSeats(request, user.getId()));
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request,
                                                           @AuthenticationPrincipal User user) {
        BookingResponse response = bookingService.createBooking(request, user.getId());
        HttpStatus status = response.getStatus().name().equals("CONFIRMED")
                ? HttpStatus.CREATED
                : HttpStatus.PAYMENT_REQUIRED; // 402 - payment failed, booking not confirmed
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Long id,
                                                        @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bookingService.getBooking(id, user.getId()));
    }

    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bookingService.getMyBookings(user.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id,
                                                           @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bookingService.cancelBooking(id, user.getId()));
    }
}
