package com.moviebooking.movie_booking.service;

import com.moviebooking.movie_booking.Entity.Movie;
import com.moviebooking.movie_booking.Repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // Create
    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    // Read all
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    // Read one
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
    }

    // Update
    public Movie updateMovie(Long id, Movie movieDetails) {
        Movie movie = getMovieById(id); // reuses the lookup + not-found check above

        movie.setTitle(movieDetails.getTitle());
        movie.setDescription(movieDetails.getDescription());
        movie.setLanguage(movieDetails.getLanguage());
        movie.setGenre(movieDetails.getGenre());
        movie.setDuration(movieDetails.getDuration());
        movie.setReleaseDate(movieDetails.getReleaseDate());
        movie.setPosterUrl(movieDetails.getPosterUrl());

        return movieRepository.save(movie);
    }

    // Delete
    public void deleteMovie(Long id) {
        Movie movie = getMovieById(id); // ensures it exists before deleting
        movieRepository.delete(movie);
    }
}