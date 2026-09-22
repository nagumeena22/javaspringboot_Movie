package com.moviebooking.movie_booking.Exception;

/**
 * NOTE: If you already created this exception in an earlier phase,
 * delete this file — don't create a duplicate class.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
