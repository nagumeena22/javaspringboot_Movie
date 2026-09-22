package com.moviebooking.movie_booking.Dto;

import com.moviebooking.movie_booking.Entity.SeatStatus;
import com.moviebooking.movie_booking.Entity.SeatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowSeatResponse {
    private Long id;
    private Long showId;
    private Long seatId;
    private String seatNumber;
    private String rowNumber;
    private SeatType seatType;
    private SeatStatus status;
    private BigDecimal price;
}
