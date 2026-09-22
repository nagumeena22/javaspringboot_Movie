package com.moviebooking.movie_booking.Dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}