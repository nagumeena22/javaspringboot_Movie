package com.moviebooking.movie_booking.service;

import com.moviebooking.movie_booking.Dto.ScreenRequest;
import com.moviebooking.movie_booking.Dto.ScreenResponse;
import com.moviebooking.movie_booking.Entity.Screen;
import com.moviebooking.movie_booking.Entity.Theatre;
import com.moviebooking.movie_booking.Exception.ResourceNotFoundException;
import com.moviebooking.movie_booking.Repository.ScreenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScreenService {

    private final ScreenRepository screenRepository;
    private final TheatreService theatreService;

    @Transactional
    public ScreenResponse createScreen(Long theatreId, ScreenRequest request) {
        Theatre theatre = theatreService.findTheatreOrThrow(theatreId);

        Screen screen = Screen.builder()
                .name(request.getName())
                .totalSeats(request.getTotalSeats())
                .theatre(theatre)
                .build();

        Screen saved = screenRepository.save(screen);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ScreenResponse> getScreensByTheatre(Long theatreId) {
        // Validates the theatre exists before listing its screens
        theatreService.findTheatreOrThrow(theatreId);
        return screenRepository.findByTheatreId(theatreId).stream()
                .map(this::toResponse)
                .toList();
    }

    // Package-private so SeatService / ShowService can validate a screenId
    Screen findScreenOrThrow(Long screenId) {
        return screenRepository.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + screenId));
    }

    private ScreenResponse toResponse(Screen screen) {
        return ScreenResponse.builder()
                .id(screen.getId())
                .name(screen.getName())
                .totalSeats(screen.getTotalSeats())
                .theatreId(screen.getTheatre().getId())
                .build();
    }
}
