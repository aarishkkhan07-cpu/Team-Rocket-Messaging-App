package com.example.team_rocket.controller;



import  com.example.team_rocket.dto.AuthResponse;
import  com.example.team_rocket.dto.LoginRequest;
import  com.example.team_rocket.dto.RegisterRequest;
import  com.example.team_rocket.service.AuthService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;



    @PostMapping("/register")
    public ResponseEntity<?> register(

            @Valid
            @RequestBody
            RegisterRequest request

    ) {

        try {

            authService.register(request);


            return ResponseEntity

                    .status(
                            HttpStatus.CREATED
                    )

                    .body(
                            Map.of(
                                    "message",
                                    "Account created successfully"
                            )
                    );

        } catch (
                IllegalArgumentException exception
        ) {

            return ResponseEntity

                    .badRequest()

                    .body(
                            Map.of(
                                    "message",
                                    exception.getMessage()
                            )
                    );
        }
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(

            @Valid
            @RequestBody
            LoginRequest request

    ) {

        try {

            AuthResponse response =
                    authService.login(
                            request
                    );


            return ResponseEntity.ok(
                    response
            );

        } catch (
                IllegalArgumentException exception
        ) {

            return ResponseEntity

                    .status(
                            HttpStatus.UNAUTHORIZED
                    )

                    .body(
                            Map.of(
                                    "message",
                                    exception.getMessage()
                            )
                    );
        }
    }




    @GetMapping("/me")
    public ResponseEntity<?> currentUser(
            Authentication authentication
    ) {

        return ResponseEntity.ok(

                Map.of(
                        "username",
                        authentication.getName()
                )
        );
    }
}