package com.moviebooking.movie_booking.Repository;

import com.moviebooking.movie_booking.Entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByScreenId(Long screenId);

    List<Show> findByScreenIdAndShowDate(Long screenId, LocalDate showDate);
}
