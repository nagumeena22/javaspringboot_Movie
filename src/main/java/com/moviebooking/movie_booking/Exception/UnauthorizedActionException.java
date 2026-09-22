package com.moviebooking.movie_booking.Exception;

/** Thrown when a user tries to view/cancel a booking or seat lock that isn't theirs. */
public class UnauthorizedActionException extends RuntimeException {
    public UnauthorizedActionException(String message) {
        super(message);
    }
}
