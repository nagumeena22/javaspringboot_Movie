package com.moviebooking.movie_booking.service;

import com.moviebooking.movie_booking.Dto.ShowSeatResponse;
import com.moviebooking.movie_booking.Entity.SeatStatus;
import com.moviebooking.movie_booking.Entity.ShowSeat;
import com.moviebooking.movie_booking.Repository.ShowSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowSeatService {

    private final ShowSeatRepository showSeatRepository;
    private final ShowService showService;

    @Transactional(readOnly = true)
    public List<ShowSeatResponse> getSeatsForShow(Long showId) {
        showService.findShowOrThrow(showId); // 404 if the show doesn't exist
        return showSeatRepository.findByShowId(showId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ShowSeatResponse> getAvailableSeatsForShow(Long showId) {
        showService.findShowOrThrow(showId);
        return showSeatRepository.findByShowIdAndStatus(showId, SeatStatus.AVAILABLE).stream()
                .map(this::toResponse)
                .toList();
    }

    private ShowSeatResponse toResponse(ShowSeat showSeat) {
        return ShowSeatResponse.builder()
                .id(showSeat.getId())
                .showId(showSeat.getShow().getId())
                .seatId(showSeat.getSeat().getId())
                .seatNumber(showSeat.getSeat().getSeatNumber())
                .rowNumber(showSeat.getSeat().getRowNumber())
                .seatType(showSeat.getSeat().getSeatType())
                .status(showSeat.getStatus())
                .price(showSeat.getPrice())
                .build();
    }
}
