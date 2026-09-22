package com.moviebooking.movie_booking.Dto;

import com.moviebooking.movie_booking.Entity.SeatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponse {
    private Long id;
    private String seatNumber;
    private String rowNumber;
    private SeatType seatType;
    private Long screenId;
}
