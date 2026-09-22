package com.moviebooking.movie_booking.Repository;

import com.moviebooking.movie_booking.Entity.SeatStatus;
import com.moviebooking.movie_booking.Entity.ShowSeat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {
    List<ShowSeat> findByShowId(Long showId);

    List<ShowSeat> findByShowIdAndStatus(Long showId, SeatStatus status);

    Optional<ShowSeat> findByShowIdAndSeatId(Long showId, Long seatId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ss from ShowSeat ss where ss.id in :ids")
    List<ShowSeat> findAllByIdForUpdate(@Param("ids") List<Long> ids);

    List<ShowSeat> findByStatusAndLockExpiryTimeBefore(SeatStatus status, LocalDateTime now);
}
