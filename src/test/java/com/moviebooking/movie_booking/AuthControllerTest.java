package com.moviebooking.movie_booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.movie_booking.Dto.*;
import com.moviebooking.movie_booking.controller.AuthController;
import com.moviebooking.movie_booking.service.AuthService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService)).build();
    }

    @Test
    void register_thenLogin_succeeds() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setName("Test User");
        register.setEmail("testuser@example.com");
        register.setPassword("password123");

        when(authService.register(any(RegisterRequest.class))).thenReturn("User registered successfully");
        when(authService.login(any(LoginRequest.class))).thenReturn(new AuthResponse("abc123", "Login successful", "CUSTOMER"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        LoginRequest login = new LoginRequest();
        login.setEmail("testuser@example.com");
        login.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("abc123"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void login_withWrongPassword_fails() throws Exception {
        LoginRequest badLogin = new LoginRequest();
        badLogin.setEmail("testuser2@example.com");
        badLogin.setPassword("wrongpass");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Bad credentials"));

        assertThrows(ServletException.class, () -> mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badLogin))));
    }
}