package com.example.team_rocket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(
            message = "Username is required"
    )
    @Size(
            min = 3,
            max = 50,
            message = "Username must be between 3 and 50 characters"
    )
    private String username;


    @NotBlank(
            message = "Password is required"
    )
    @Size(
            min = 6,
            max = 100,
            message = "Password must contain at least 6 characters"
    )
    private String password;


    @NotBlank(
            message = "Confirm password is required"
    )
    private String confirmPassword;
}