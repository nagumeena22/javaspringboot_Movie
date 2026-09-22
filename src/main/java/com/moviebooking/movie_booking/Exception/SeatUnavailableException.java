package com.moviebooking.movie_booking.Exception;

/** Thrown when a seat is already LOCKED by someone else or already BOOKED. */
public class SeatUnavailableException extends RuntimeException {
    public SeatUnavailableException(String message) {
        super(message);
    }
}
