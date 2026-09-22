package com.moviebooking.movie_booking.controller;

import com.moviebooking.movie_booking.Dto.ScreenRequest;
import com.moviebooking.movie_booking.Dto.ScreenResponse;
import com.moviebooking.movie_booking.service.ScreenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theatres/{theatreId}/screens")
@RequiredArgsConstructor
public class ScreenController {

    private final ScreenService screenService;

    @PostMapping
    public ResponseEntity<ScreenResponse> createScreen(@PathVariable Long theatreId,
                                                         @RequestBody ScreenRequest request) {
        return new ResponseEntity<>(screenService.createScreen(theatreId, request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ScreenResponse>> getScreensByTheatre(@PathVariable Long theatreId) {
        return ResponseEntity.ok(screenService.getScreensByTheatre(theatreId));
    }
}
