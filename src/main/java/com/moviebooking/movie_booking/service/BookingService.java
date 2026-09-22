package com.moviebooking.movie_booking.service;

import com.moviebooking.movie_booking.Dto.*;
import com.moviebooking.movie_booking.Entity.*;
import com.moviebooking.movie_booking.Exception.ResourceNotFoundException;
import com.moviebooking.movie_booking.Exception.UnauthorizedActionException;
import com.moviebooking.movie_booking.Repository.BookingRepository;
import com.moviebooking.movie_booking.Repository.ShowRepository;
import com.moviebooking.movie_booking.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;     // from Phase 1
    private final ShowRepository showRepository;     // from Phase 2/3
    private final SeatLockService seatLockService;
    private final PaymentService paymentService;

    // ---------- Step 1: lock seats while the customer is on the seat-map / payment screen ----------

    @Transactional
    public LockSeatsResponse lockSeats(LockSeatsRequest request, Long userId) {
        List<Long> locked = seatLockService.lockSeats(request.getShowId(), request.getShowSeatIds(), userId);
        return LockSeatsResponse.builder()
                .lockedShowSeatIds(locked)
                .lockExpiryTime(LocalDateTime.now().plusMinutes(SeatLockService.LOCK_DURATION_MINUTES))
                .lockDurationSeconds(SeatLockService.LOCK_DURATION_MINUTES * 60L)
                .build();
    }

    // ---------- Step 2: create the booking, take payment, confirm or roll back ----------

    @Transactional
    public BookingResponse createBooking(BookingRequest request, Long userId) {
        // 1. Make sure the caller actually holds a live lock on every seat requested.
        List<ShowSeat> seats = seatLockService.validateLockOwnership(request.getShowSeatIds(), userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found: " + request.getShowId()));

        BigDecimal totalAmount = seats.stream()
                .map(ShowSeat::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Create the booking shell first (status decided after payment).
        Booking booking = Booking.builder()
                .user(user)
                .show(show)
                .totalAmount(totalAmount)
                .bookingDate(LocalDateTime.now())
                .status(BookingStatus.CANCELLED) // placeholder, corrected below
                .build();

        for (ShowSeat seat : seats) {
            booking.addBookingSeat(BookingSeat.builder().showSeat(seat).build());
        }

        // 3. Take payment (mock gateway - see PaymentService).
        Payment payment = paymentService.processPayment(booking, request.getPaymentMethod());

        // 4. Confirm or roll back based on the payment result.
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            booking.setStatus(BookingStatus.CONFIRMED);
            seatLockService.confirmSeats(seats);
        } else {
            booking.setStatus(BookingStatus.CANCELLED);
            seatLockService.releaseSeats(seats);
        }

        booking.setPayment(payment);
        payment.setBooking(booking);
        Booking saved = bookingRepository.save(booking);

        return toResponse(saved);
    }

    // ---------- Read endpoints ----------

    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            throw new UnauthorizedActionException("This booking does not belong to you");
        }
        return toResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByBookingDateDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ---------- Cancellation ----------

    @Transactional
    public BookingResponse cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            throw new UnauthorizedActionException("This booking does not belong to you");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return toResponse(booking); // already cancelled, idempotent
        }

        // Free up the seats for other customers.
        List<ShowSeat> seats = booking.getBookingSeats().stream()
                .map(BookingSeat::getShowSeat)
                .toList();
        seatLockService.releaseSeats(seats);

        booking.setStatus(BookingStatus.CANCELLED);

        if (booking.getPayment() != null && booking.getPayment().getStatus() == PaymentStatus.SUCCESS) {
            // Mock refund - mark the existing payment record as refunded.
            booking.getPayment().setStatus(PaymentStatus.REFUNDED);
        }

        return toResponse(bookingRepository.save(booking));
    }

    // ---------- Mapping ----------

    private BookingResponse toResponse(Booking booking) {
        List<String> seatNumbers = booking.getBookingSeats().stream()
                .map(bs -> describeSeat(bs.getShowSeat()))
                .toList();

        PaymentResponse paymentResponse = null;
        if (booking.getPayment() != null) {
            Payment p = booking.getPayment();
            paymentResponse = PaymentResponse.builder()
                    .paymentId(p.getId())
                    .amount(p.getAmount())
                    .paymentMethod(p.getPaymentMethod())
                    .transactionId(p.getTransactionId())
                    .status(p.getStatus())
                    .paymentDate(p.getPaymentDate())
                    .build();
        }

        return BookingResponse.builder()
                .bookingId(booking.getId())
                .showId(booking.getShow().getId())
                // Adjust these two getters to match your Movie/Show entity chain
                .movieTitle(booking.getShow().getMovie().getTitle())
                .screenName(booking.getShow().getScreen().getName())
                .showTime(booking.getShow().getShowTime())
                .seatNumbers(seatNumbers)
                .totalAmount(booking.getTotalAmount())
                .bookingDate(booking.getBookingDate())
                .status(booking.getStatus())
                .payment(paymentResponse)
                .build();
    }

    private String describeSeat(ShowSeat seat) {
        // Adjust to however your Seat entity exposes row/number, e.g. seat.getSeat().getSeatNumber()
        return seat.getSeatNumber();
    }
}
