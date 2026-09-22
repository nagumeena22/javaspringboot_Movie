package com.moviebooking.movie_booking.controller;

import com.moviebooking.movie_booking.Dto.ShowRequest;
import com.moviebooking.movie_booking.Dto.ShowResponse;
import com.moviebooking.movie_booking.Dto.ShowSeatResponse;
import com.moviebooking.movie_booking.service.ShowSeatService;
import com.moviebooking.movie_booking.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;
    private final ShowSeatService showSeatService;

    @PostMapping
    public ResponseEntity<ShowResponse> createShow(@RequestBody ShowRequest request) {
        return new ResponseEntity<>(showService.createShow(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ShowResponse>> getAllShows() {
        return ResponseEntity.ok(showService.getAllShows());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowResponse> getShowById(@PathVariable Long id) {
        return ResponseEntity.ok(showService.getShowById(id));
    }

    // All seats for this show (AVAILABLE + BOOKED)
    @GetMapping("/{id}/seats")
    public ResponseEntity<List<ShowSeatResponse>> getSeatsForShow(@PathVariable Long id) {
        return ResponseEntity.ok(showSeatService.getSeatsForShow(id));
    }

    // Only AVAILABLE seats — what Phase 4's booking flow will query first
    @GetMapping("/{id}/seats/available")
    public ResponseEntity<List<ShowSeatResponse>> getAvailableSeatsForShow(@PathVariable Long id) {
        return ResponseEntity.ok(showSeatService.getAvailableSeatsForShow(id));
    }
}
