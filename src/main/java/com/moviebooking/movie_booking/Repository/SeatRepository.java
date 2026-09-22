package com.moviebooking.movie_booking.Repository;

import com.moviebooking.movie_booking.Entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByScreenId(Long screenId);

    boolean existsByScreenIdAndSeatNumber(Long screenId, String seatNumber);
}
