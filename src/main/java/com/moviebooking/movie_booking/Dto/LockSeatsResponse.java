package com.moviebooking.movie_booking.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LockSeatsResponse {
    private List<Long> lockedShowSeatIds;
    private LocalDateTime lockExpiryTime;
    private long lockDurationSeconds;
}
