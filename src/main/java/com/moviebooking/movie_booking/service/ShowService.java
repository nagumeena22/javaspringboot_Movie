package com.moviebooking.movie_booking.service;

import com.moviebooking.movie_booking.Dto.ShowRequest;
import com.moviebooking.movie_booking.Dto.ShowResponse;
import com.moviebooking.movie_booking.Entity.*;
import com.moviebooking.movie_booking.Exception.BadRequestException;
import com.moviebooking.movie_booking.Exception.ResourceNotFoundException;
import com.moviebooking.movie_booking.Repository.MovieRepository;
import com.moviebooking.movie_booking.Repository.SeatRepository;
import com.moviebooking.movie_booking.Repository.ShowRepository;
import com.moviebooking.movie_booking.Repository.ShowSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * NOTE: MovieRepository / Movie are assumed to already exist from Phase 1,
 * living in com.cinema.booking.repository / com.cinema.booking.entity.
 * If your Phase 1 package names differ, update the imports above.
 */
@Service
@RequiredArgsConstructor
public class ShowService {

    // Surcharge applied to PREMIUM seats over the show's base ticket price.
    private static final BigDecimal PREMIUM_MULTIPLIER = new BigDecimal("1.5");

    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final SeatRepository seatRepository;
    private final MovieRepository movieRepository;
    private final ScreenService screenService;

    @Transactional
    public ShowResponse createShow(ShowRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + request.getMovieId()));

        Screen screen = screenService.findScreenOrThrow(request.getScreenId());

        if (request.getEndTime() != null && request.getStartTime() != null
                && !request.getEndTime().isAfter(request.getStartTime())) {
            throw new BadRequestException("endTime must be after startTime");
        }

        Show show = Show.builder()
                .movie(movie)
                .screen(screen)
                .showDate(request.getShowDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .ticketPrice(request.getTicketPrice())
                .build();

        Show savedShow = showRepository.save(show);

        // Auto-generate a ShowSeat (AVAILABLE) for every physical seat on this screen.
        List<Seat> seats = seatRepository.findByScreenId(screen.getId());
        if (seats.isEmpty()) {
            throw new BadRequestException(
                    "Screen " + screen.getId() + " has no seats yet. Add seats before creating a show.");
        }

        List<ShowSeat> showSeats = seats.stream()
                .map(seat -> ShowSeat.builder()
                        .show(savedShow)
                        .seat(seat)
                        .status(SeatStatus.AVAILABLE)
                        .price(priceForSeat(seat, savedShow.getTicketPrice()))
                        .build())
                .toList();

        showSeatRepository.saveAll(showSeats);

        return toResponse(savedShow);
    }

    @Transactional(readOnly = true)
    public List<ShowResponse> getAllShows() {
        return showRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ShowResponse getShowById(Long id) {
        return toResponse(findShowOrThrow(id));
    }

    // Package-private so ShowSeatService can validate a showId
    Show findShowOrThrow(Long id) {
        return showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + id));
    }

    private BigDecimal priceForSeat(Seat seat, BigDecimal basePrice) {
        if (seat.getSeatType() == SeatType.PREMIUM) {
            return basePrice.multiply(PREMIUM_MULTIPLIER).setScale(2, RoundingMode.HALF_UP);
        }
        return basePrice.setScale(2, RoundingMode.HALF_UP);
    }

    private ShowResponse toResponse(Show show) {
        return ShowResponse.builder()
                .id(show.getId())
                .movieId(show.getMovie().getId())
                .movieTitle(show.getMovie().getTitle())
                .screenId(show.getScreen().getId())
                .screenName(show.getScreen().getName())
                .theatreId(show.getScreen().getTheatre().getId())
                .theatreName(show.getScreen().getTheatre().getName())
                .showDate(show.getShowDate())
                .startTime(show.getStartTime())
                .endTime(show.getEndTime())
                .ticketPrice(show.getTicketPrice())
                .build();
    }
}
