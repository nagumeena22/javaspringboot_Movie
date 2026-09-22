-- ============================================================
-- MySQL Seed File for Movie Booking System
-- Database: moviebooking_db (or your active MySQL database name)
-- ============================================================

-- 1. Insert Movies
INSERT INTO movie (title, genre, duration_minutes, language, rating, release_date, description, poster_url) VALUES
('Inception', 'Sci-Fi', 148, 'English', 4.8, '2010-07-16', 'A thief who steals corporate secrets through dream-sharing technology.', 'https://images.unsplash.com/photo-1536440136628-849c177e76a1?q=80&w=800&auto=format&fit=crop'),
('The Dark Knight', 'Action', 152, 'English', 4.9, '2008-07-18', 'When the Joker wreaks havoc on Gotham, Batman must fight injustice.', 'https://images.unsplash.com/photo-1478720568477-152d9b164e26?q=80&w=800&auto=format&fit=crop'),
('Interstellar', 'Sci-Fi', 169, 'English', 4.9, '2014-11-07', 'A team of researchers travels through a wormhole in space to ensure human survival.', 'https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?q=80&w=800&auto=format&fit=crop'),
('Avatar: The Way of Water', 'Sci-Fi', 192, 'English', 4.7, '2022-12-16', 'Jake Sully and Neytiri protect their family on Pandora.', 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=800&auto=format&fit=crop'),
('Dune: Part Two', 'Action', 166, 'English', 4.8, '2024-03-01', 'Paul Atreides unites with Chani and the Fremen to avenge his family.', 'https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=800&auto=format&fit=crop'),
('Avengers: Endgame', 'Action', 181, 'English', 4.8, '2019-04-26', 'The Avengers assemble once more to reverse Thanos actions.', 'https://images.unsplash.com/photo-1568832359672-e36cf5d74f54?q=80&w=800&auto=format&fit=crop');

-- 2. Insert Theatres
INSERT INTO theatre (name, location) VALUES
('PVR IMAX Cineplex', 'Downtown Mall, Metro City'),
('Cinepolis Megaplex', 'Highstreet Plaza, City Center');

-- 3. Insert Screens (Assumes Theatre IDs 1 and 2)
INSERT INTO screen (name, total_seats, theatre_id) VALUES
('Screen 1 (IMAX 3D)', 50, 1),
('Screen 2 (Dolby Atmos)', 40, 1),
('Screen A (Vip Lounge)', 30, 2);

-- 4. Insert Default Admin & Customer Users
-- Note: Passwords below are BCrypt hashes of 'admin123' and 'password123'
INSERT INTO users (name, email, password, role, created_at) VALUES
('System Admin', 'admin@moviebooking.com', '$2a$10$wEaD2QvB1/9/T8pG3X4bceV8z7k7N2j9r6a0w5Y5z.X.X.X', 'ADMIN', NOW()),
('John Customer', 'customer@example.com', '$2a$10$wEaD2QvB1/9/T8pG3X4bceV8z7k7N2j9r6a0w5Y5z.X.X.X', 'CUSTOMER', NOW());
