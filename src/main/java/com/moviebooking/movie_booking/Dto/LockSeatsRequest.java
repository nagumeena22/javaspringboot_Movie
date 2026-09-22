package com.moviebooking.movie_booking.Dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LockSeatsRequest {

    @NotNull
    private Long showId;

    @NotEmpty(message = "Select at least one seat")
    private List<Long> showSeatIds;
}
