package com.moviebooking.movie_booking.Dto;

import com.moviebooking.movie_booking.Entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Long bookingId;
    private Long showId;
    private String movieTitle;   // convenience field for the UI
    private String screenName;
    private LocalDateTime showTime;
    private List<String> seatNumbers; // e.g. ["A1", "A2"]
    private BigDecimal totalAmount;
    private LocalDateTime bookingDate;
    private BookingStatus status;
    private PaymentResponse payment;
}
