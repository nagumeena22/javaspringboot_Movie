package com.moviebooking.movie_booking.config;

import com.moviebooking.movie_booking.Dto.BulkSeatRequest;
import com.moviebooking.movie_booking.Dto.ScreenRequest;
import com.moviebooking.movie_booking.Dto.SeatRequest;
import com.moviebooking.movie_booking.Dto.ShowRequest;
import com.moviebooking.movie_booking.Dto.TheatreRequest;
import com.moviebooking.movie_booking.Entity.Movie;
import com.moviebooking.movie_booking.Entity.Role;
import com.moviebooking.movie_booking.Entity.SeatType;
import com.moviebooking.movie_booking.Entity.User;
import com.moviebooking.movie_booking.Repository.MovieRepository;
import com.moviebooking.movie_booking.Repository.UserRepository;
import com.moviebooking.movie_booking.service.ScreenService;
import com.moviebooking.movie_booking.service.SeatService;
import com.moviebooking.movie_booking.service.ShowService;
import com.moviebooking.movie_booking.service.TheatreService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TheatreService theatreService;
    private final ScreenService screenService;
    private final SeatService seatService;
    private final ShowService showService;

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Users if empty
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .name("System Admin")
                    .email("admin@moviebooking.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);

            User customer = User.builder()
                    .name("John Customer")
                    .email("customer@example.com")
                    .password(passwordEncoder.encode("password123"))
                    .role(Role.CUSTOMER)
                    .build();
            userRepository.save(customer);

            System.out.println("✅ Default Admin & Customer users seeded.");
        }

        // 2. Seed Movies if empty
        if (movieRepository.count() == 0) {
            Movie m1 = new Movie(
                    "Inception",
                    "A thief who steals corporate secrets through the use of dream-sharing technology.",
                    "English",
                    "Sci-Fi",
                    148,
                    LocalDate.of(2010, 7, 16),
                    "https://images.unsplash.com/photo-1536440136628-849c177e76a1?q=80&w=800&auto=format&fit=crop"
            );

            Movie m2 = new Movie(
                    "The Dark Knight",
                    "When the Joker wreaks havoc on Gotham, Batman must accept a great psychological test.",
                    "English",
                    "Action",
                    152,
                    LocalDate.of(2008, 7, 18),
                    "https://images.unsplash.com/photo-1478720568477-152d9b164e26?q=80&w=800&auto=format&fit=crop"
            );

            Movie m3 = new Movie(
                    "Interstellar",
                    "A team of researchers travels through a wormhole in space to ensure human survival.",
                    "English",
                    "Sci-Fi",
                    169,
                    LocalDate.of(2014, 11, 7),
                    "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?q=80&w=800&auto=format&fit=crop"
            );

            Movie m4 = new Movie(
                    "Avatar: The Way of Water",
                    "Jake Sully lives with his family on Pandora. When a threat returns, Jake works with Neytiri.",
                    "English",
                    "Sci-Fi",
                    192,
                    LocalDate.of(2022, 12, 16),
                    "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=800&auto=format&fit=crop"
            );

            Movie m5 = new Movie(
                    "Dune: Part Two",
                    "Paul Atreides unites with Chani and the Fremen while seeking revenge against conspirators.",
                    "English",
                    "Action",
                    166,
                    LocalDate.of(2024, 3, 1),
                    "https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=800&auto=format&fit=crop"
            );

            List<Movie> savedMovies = movieRepository.saveAll(List.of(m1, m2, m3, m4, m5));
            System.out.println("✅ " + savedMovies.size() + " Movies seeded into backend database.");

            // 3. Seed Theatre, Screen, Seats & Shows
            try {
                TheatreRequest tr = new TheatreRequest();
                tr.setName("PVR IMAX Cineplex");
                tr.setLocation("Downtown Mall, Metro City");
                var theatre = theatreService.createTheatre(tr);

                ScreenRequest sr = new ScreenRequest();
                sr.setName("Screen 1 (IMAX 3D)");
                sr.setTotalSeats(40);
                var screen = screenService.createScreen(theatre.getId(), sr);

                // Create Seats A1..A10, B1..B10, C1..C10, D1..D10
                List<SeatRequest> seatsList = new ArrayList<>();
                String[] rows = {"A", "B", "C", "D"};
                for (String r : rows) {
                    SeatType stype = (r.equals("A") || r.equals("B")) ? SeatType.PREMIUM : SeatType.REGULAR;
                    for (int i = 1; i <= 10; i++) {
                        SeatRequest seatReq = new SeatRequest();
                        seatReq.setSeatNumber(r + i);
                        seatReq.setRowNumber(r);
                        seatReq.setSeatType(stype);
                        seatsList.add(seatReq);
                    }
                }
                BulkSeatRequest bulkSeats = new BulkSeatRequest();
                bulkSeats.setSeats(seatsList);
                seatService.createSeats(screen.getId(), bulkSeats);

                // Create Shows for saved movies
                for (int idx = 0; idx < Math.min(3, savedMovies.size()); idx++) {
                    Movie movie = savedMovies.get(idx);
                    ShowRequest showReq = new ShowRequest();
                    showReq.setMovieId(movie.getId());
                    showReq.setScreenId(screen.getId());
                    showReq.setShowDate(LocalDate.now());
                    showReq.setStartTime(LocalTime.of(14 + (idx * 3), 0));
                    showReq.setEndTime(LocalTime.of(17 + (idx * 3), 0));
                    showReq.setTicketPrice(BigDecimal.valueOf(250 + (idx * 50)));
                    showService.createShow(showReq);
                }
                System.out.println("✅ Theatres, Screens, Seats, and Showtimes initialized.");
            } catch (Exception e) {
                System.out.println("Notice on Initializer seeding: " + e.getMessage());
            }
        }
    }
}
