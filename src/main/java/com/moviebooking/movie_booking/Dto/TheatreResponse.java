package com.moviebooking.movie_booking.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TheatreResponse {
    private Long id;
    private String name;
    private String location;
    private int screenCount;
}
