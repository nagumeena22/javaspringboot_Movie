package com.moviebooking.movie_booking.Dto;

import com.moviebooking.movie_booking.Entity.SeatType;
import lombok.Data;

@Data
public class SeatRequest {
    private String seatNumber; // e.g. "A1"
    private String rowNumber;  // e.g. "A"
    private SeatType seatType; // REGULAR / PREMIUM
}
