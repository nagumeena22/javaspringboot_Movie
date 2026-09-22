package com.moviebooking.movie_booking.controller;

import com.moviebooking.movie_booking.Dto.BulkSeatRequest;
import com.moviebooking.movie_booking.Dto.SeatResponse;
import com.moviebooking.movie_booking.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Seats are always created in the context of a screen, so both endpoints
 * are nested under /api/screens/{screenId}/seats rather than /api/seats.
 */
@RestController
@RequestMapping("/api/screens/{screenId}/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @PostMapping
    public ResponseEntity<List<SeatResponse>> createSeats(@PathVariable Long screenId,
                                                            @RequestBody BulkSeatRequest request) {
        return new ResponseEntity<>(seatService.createSeats(screenId, request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SeatResponse>> getSeatsByScreen(@PathVariable Long screenId) {
        return ResponseEntity.ok(seatService.getSeatsByScreen(screenId));
    }
}
