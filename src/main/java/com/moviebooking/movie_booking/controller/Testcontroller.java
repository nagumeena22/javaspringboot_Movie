package com.moviebooking.movie_booking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Testcontroller {

    @GetMapping("/hello")
    public String hello() {
        return "Movie Booking Backend is Running!";
    }
    
    @GetMapping("/meena")
    public int add(){
        return 3+4;
    }

    @GetMapping("/myson")
    public String myson(){
        return "My son is my world";
    }
}
