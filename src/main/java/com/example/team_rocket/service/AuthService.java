package com.example.team_rocket.service;



import com.example.team_rocket.dto.AuthResponse;
import com.example.team_rocket.dto.LoginRequest;
import com.example.team_rocket.dto.RegisterRequest;
import com.example.team_rocket.entity.User;
import com.example.team_rocket.repository.UserRepository;
import com.example.team_rocket.security.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;


    // ==========================================
    // REGISTER
    // ==========================================

    public void register(
            RegisterRequest request
    ) {

        String username =
                request.getUsername()
                        .trim();


        /*
         * Confirm password.
         */

        if (
                !request.getPassword()
                        .equals(
                                request.getConfirmPassword()
                        )
        ) {

            throw new IllegalArgumentException(
                    "Passwords do not match"
            );
        }


        /*
         * Check duplicate username.
         */

        if (
                userRepository
                        .existsByUsername(username)
        ) {

            throw new IllegalArgumentException(
                    "Username is already taken"
            );
        }


        /*
         * BCrypt password hashing.
         */

        String encodedPassword =
                passwordEncoder.encode(
                        request.getPassword()
                );


        /*
         * Create User.
         */

        User user =
                new User();

        user.setUsername(username);

        user.setPassword(encodedPassword);


        /*
         * Save to H2.
         */

        userRepository.save(user);
    }


    // ==========================================
    // LOGIN
    // ==========================================

    public AuthResponse login(
            LoginRequest request
    ) {

        String username =
                request.getUsername()
                        .trim();


        /*
         * Find user.
         */

        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Incorrect username or password"
                                        )
                        );


        /*
         * Compare password with BCrypt hash.
         */

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );


        if (!passwordMatches) {

            throw new IllegalArgumentException(
                    "Incorrect username or password"
            );
        }


        /*
         * Generate JWT.
         */

        String token =
                jwtService.generateToken(
                        user.getUsername()
                );


        /*
         * Return JWT response.
         */

        return new AuthResponse(

                user.getId(),

                user.getUsername(),

                token
        );
    }
}