package com.moviebooking.movie_booking.service;

import com.moviebooking.movie_booking.Dto.TheatreRequest;
import com.moviebooking.movie_booking.Dto.TheatreResponse;
import com.moviebooking.movie_booking.Entity.Theatre;
import com.moviebooking.movie_booking.Exception.ResourceNotFoundException;
import com.moviebooking.movie_booking.Repository.TheatreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TheatreService {

    private final TheatreRepository theatreRepository;

    @Transactional
    public TheatreResponse createTheatre(TheatreRequest request) {
        Theatre theatre = Theatre.builder()
                .name(request.getName())
                .location(request.getLocation())
                .build();

        Theatre saved = theatreRepository.save(theatre);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TheatreResponse> getAllTheatres() {
        return theatreRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TheatreResponse getTheatreById(Long id) {
        Theatre theatre = findTheatreOrThrow(id);
        return toResponse(theatre);
    }

    @Transactional
    public TheatreResponse updateTheatre(Long id, TheatreRequest request) {
        Theatre theatre = findTheatreOrThrow(id);
        theatre.setName(request.getName());
        theatre.setLocation(request.getLocation());
        Theatre saved = theatreRepository.save(theatre);
        return toResponse(saved);
    }

    @Transactional
    public void deleteTheatre(Long id) {
        Theatre theatre = findTheatreOrThrow(id);
        theatreRepository.delete(theatre);
    }

    // Package-private so ScreenService can reuse it to validate a theatreId
    Theatre findTheatreOrThrow(Long id) {
        return theatreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + id));
    }

    private TheatreResponse toResponse(Theatre theatre) {
        return TheatreResponse.builder()
                .id(theatre.getId())
                .name(theatre.getName())
                .location(theatre.getLocation())
                .screenCount(theatre.getScreens() == null ? 0 : theatre.getScreens().size())
                .build();
    }
}
