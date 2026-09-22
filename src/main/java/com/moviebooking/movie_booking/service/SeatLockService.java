package com.moviebooking.movie_booking.service;

import com.moviebooking.movie_booking.Entity.SeatStatus;
import com.moviebooking.movie_booking.Entity.ShowSeat;
import com.moviebooking.movie_booking.Exception.SeatUnavailableException;
import com.moviebooking.movie_booking.Repository.ShowSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatLockService {

    private final ShowSeatRepository showSeatRepository;

    /** How long a lock is held before it auto-releases if payment never completes. */
    public static final int LOCK_DURATION_MINUTES = 5;

    /**
     * Locks the given seats for this user, or throws if any seat is already
     * BOOKED, or LOCKED by someone else with a lock that hasn't expired yet.
     *
     * PESSIMISTIC_WRITE row locking (see ShowSeatRepository.findAllByIdForUpdate)
     * closes the race window between "check status" and "set status" so two
     * concurrent requests for the same seat can't both succeed.
     */
    @Transactional
    public List<Long> lockSeats(Long showId, List<Long> showSeatIds, Long userId) {
        List<ShowSeat> seats = showSeatRepository.findAllByIdForUpdate(showSeatIds);

        if (seats.size() != showSeatIds.size()) {
            throw new SeatUnavailableException("One or more selected seats do not exist for this show");
        }

        LocalDateTime now = LocalDateTime.now();

        for (ShowSeat seat : seats) {
            if (!seat.getShow().getId().equals(showId)) {
                throw new SeatUnavailableException("Seat " + seat.getId() + " does not belong to the selected show");
            }

            boolean isBooked = seat.getStatus() == SeatStatus.BOOKED;
            boolean isLockedByOther = seat.getStatus() == SeatStatus.LOCKED
                    && seat.getLockExpiryTime() != null
                    && seat.getLockExpiryTime().isAfter(now)
                    && !userId.equals(seat.getLockedByUserId());

            if (isBooked || isLockedByOther) {
                throw new SeatUnavailableException(
                        "Seat " + describeSeat(seat) + " is no longer available");
            }
        }

        // All checks passed - lock every seat for this user.
        for (ShowSeat seat : seats) {
            seat.setStatus(SeatStatus.LOCKED);
            seat.setLockedByUserId(userId);
            seat.setLockExpiryTime(now.plusMinutes(LOCK_DURATION_MINUTES));
        }
        showSeatRepository.saveAll(seats);

        return seats.stream().map(ShowSeat::getId).toList();
    }

    /** Validates the caller actually holds a live lock on every seat before booking proceeds. */
    @Transactional(readOnly = true)
    public List<ShowSeat> validateLockOwnership(List<Long> showSeatIds, Long userId) {
        List<ShowSeat> seats = showSeatRepository.findAllById(showSeatIds);
        LocalDateTime now = LocalDateTime.now();

        for (ShowSeat seat : seats) {
            boolean ownsValidLock = seat.getStatus() == SeatStatus.LOCKED
                    && userId.equals(seat.getLockedByUserId())
                    && seat.getLockExpiryTime() != null
                    && seat.getLockExpiryTime().isAfter(now);

            if (!ownsValidLock) {
                throw new SeatUnavailableException(
                        "Your lock on seat " + describeSeat(seat) + " has expired. Please reselect your seats.");
            }
        }
        return seats;
    }

    /** Marks seats BOOKED and clears lock metadata - called after successful payment. */
    @Transactional
    public void confirmSeats(List<ShowSeat> seats) {
        for (ShowSeat seat : seats) {
            seat.setStatus(SeatStatus.BOOKED);
            seat.setLockedByUserId(null);
            seat.setLockExpiryTime(null);
        }
        showSeatRepository.saveAll(seats);
    }

    /** Releases seats back to AVAILABLE - called on payment failure or manual cancellation. */
    @Transactional
    public void releaseSeats(List<ShowSeat> seats) {
        for (ShowSeat seat : seats) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setLockedByUserId(null);
            seat.setLockExpiryTime(null);
        }
        showSeatRepository.saveAll(seats);
    }

    @Transactional
    public void releaseSeatsById(List<Long> showSeatIds) {
        releaseSeats(showSeatRepository.findAllById(showSeatIds));
    }

    /**
     * Background sweep: runs every minute and returns any seat whose lock
     * expired (customer abandoned checkout / payment page timed out) back
     * to AVAILABLE. This is what stops an ignored lock from blocking a seat
     * forever. Register with @EnableScheduling (see config/SchedulingConfig).
     */
    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void releaseExpiredLocks() {
        List<ShowSeat> expired = showSeatRepository.findByStatusAndLockExpiryTimeBefore(
                SeatStatus.LOCKED, LocalDateTime.now());

        if (!expired.isEmpty()) {
            releaseSeats(expired);
            log.info("Released {} expired seat lock(s)", expired.size());
        }
    }

    private String describeSeat(ShowSeat seat) {
        // Adjust to however your Seat entity exposes row/number, e.g. seat.getSeat().getSeatNumber()
        return String.valueOf(seat.getId());
    }
}
