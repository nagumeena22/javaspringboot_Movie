package com.moviebooking.movie_booking.service;

import com.moviebooking.movie_booking.Dto.BulkSeatRequest;
import com.moviebooking.movie_booking.Dto.SeatRequest;
import com.moviebooking.movie_booking.Dto.SeatResponse;
import com.moviebooking.movie_booking.Entity.Screen;
import com.moviebooking.movie_booking.Entity.Seat;
import com.moviebooking.movie_booking.Exception.BadRequestException;
import com.moviebooking.movie_booking.Repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final ScreenService screenService;

    /**
     * Bulk-create seats for a screen, e.g. A1..A5, B1..B5, C1..C5.
     * Rejects duplicate seat numbers on the same screen.
     */
    @Transactional
    public List<SeatResponse> createSeats(Long screenId, BulkSeatRequest request) {
        Screen screen = screenService.findScreenOrThrow(screenId);

        if (request.getSeats() == null || request.getSeats().isEmpty()) {
            throw new BadRequestException("At least one seat must be provided");
        }

        List<Seat> seatsToSave = request.getSeats().stream()
                .map(seatRequest -> {
                    if (seatRepository.existsByScreenIdAndSeatNumber(screenId, seatRequest.getSeatNumber())) {
                        throw new BadRequestException(
                                "Seat " + seatRequest.getSeatNumber() + " already exists on screen " + screenId);
                    }
                    return Seat.builder()
                            .seatNumber(seatRequest.getSeatNumber())
                            .rowNumber(seatRequest.getRowNumber())
                            .seatType(seatRequest.getSeatType())
                            .screen(screen)
                            .build();
                })
                .toList();

        List<Seat> saved = seatRepository.saveAll(seatsToSave);
        return saved.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<SeatResponse> getSeatsByScreen(Long screenId) {
        screenService.findScreenOrThrow(screenId);
        return seatRepository.findByScreenId(screenId).stream()
                .map(this::toResponse)
                .toList();
    }

    private SeatResponse toResponse(Seat seat) {
        return SeatResponse.builder()
                .id(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .rowNumber(seat.getRowNumber())
                .seatType(seat.getSeatType())
                .screenId(seat.getScreen().getId())
                .build();
    }
}
