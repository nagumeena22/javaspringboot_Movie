package com.moviebooking.movie_booking.Dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Sent to POST /api/bookings AFTER the seats have already been
 * locked via POST /api/bookings/seats/lock.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @NotNull
    private Long showId;

    @NotEmpty(message = "Select at least one seat")
    private List<Long> showSeatIds;

    @NotNull(message = "Payment method is required")
    private String paymentMethod; // "CARD" / "UPI" / "NETBANKING" / "WALLET"
}
