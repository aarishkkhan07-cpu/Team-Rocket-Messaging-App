package com.example.team_rocket.service;

import com.example.team_rocket.dto.AuthResponse;
import com.example.team_rocket.dto.LoginRequest;
import com.example.team_rocket.dto.RegisterRequest;
import com.example.team_rocket.entity.User;
import com.example.team_rocket.repository.UserRepository;
import com.example.team_rocket.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTests {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void registerHashesPasswordAndSavesTrimmedUsername() {
        RegisterRequest request = registerRequest("  alice  ", "secret1", "secret1");
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(passwordEncoder.encode("secret1")).thenReturn("hashed-password");

        authService.register(request);

        verify(userRepository).save(argThat(user ->
                "alice".equals(user.getUsername())
                        && "hashed-password".equals(user.getPassword())));
    }

    @Test
    void registerRejectsPasswordMismatch() {
        RegisterRequest request = registerRequest("alice", "secret1", "different");

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> authService.register(request));

        assertEquals("Passwords do not match", exception.getMessage());
        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerRejectsDuplicateUsername() {
        RegisterRequest request = registerRequest("alice", "secret1", "secret1");
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> authService.register(request));

        assertEquals("Username is already taken", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginReturnsTokenForValidCredentials() {
        User user = new User(7L, "alice", "hashed-password");
        LoginRequest request = loginRequest(" alice ", "secret1");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret1", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken("alice")).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertEquals(7L, response.getUserId());
        assertEquals("alice", response.getUsername());
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void loginRejectsUnknownUser() {
        LoginRequest request = loginRequest("unknown", "secret1");
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> authService.login(request));

        assertEquals("Incorrect username or password", exception.getMessage());
        verifyNoInteractions(jwtService);
    }

    @Test
    void loginRejectsIncorrectPassword() {
        User user = new User(7L, "alice", "hashed-password");
        LoginRequest request = loginRequest("alice", "wrong");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed-password")).thenReturn(false);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> authService.login(request));

        assertEquals("Incorrect username or password", exception.getMessage());
        verify(jwtService, never()).generateToken(anyString());
    }

    private RegisterRequest registerRequest(String username, String password, String confirmPassword) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        return request;
    }

    private LoginRequest loginRequest(String username, String password) {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }
}
