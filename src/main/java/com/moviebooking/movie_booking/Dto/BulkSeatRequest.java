package com.moviebooking.movie_booking.Dto;

import lombok.Data;

import java.util.List;

/**
 * Wrapper used to create many seats for a screen in one call.
 * Example JSON:
 * {
 *   "seats": [
 *     { "seatNumber": "A1", "rowNumber": "A", "seatType": "REGULAR" },
 *     { "seatNumber": "A2", "rowNumber": "A", "seatType": "REGULAR" },
 *     { "seatNumber": "A3", "rowNumber": "A", "seatType": "PREMIUM" }
 *   ]
 * }
 */
@Data
public class BulkSeatRequest {
    private List<SeatRequest> seats;
}
