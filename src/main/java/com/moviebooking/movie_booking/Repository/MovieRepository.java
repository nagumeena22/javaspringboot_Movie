package com.moviebooking.movie_booking.Repository;

import com.moviebooking.movie_booking.Entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    // JpaRepository already gives us:
    // save(), findAll(), findById(), deleteById(), existsById(), count(), etc.
    // We can add custom query methods here later, e.g.:
    // List<Movie> findByGenre(String genre);
}