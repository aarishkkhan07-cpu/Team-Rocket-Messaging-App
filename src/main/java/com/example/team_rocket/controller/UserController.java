package com.example.team_rocket.controller;

import com.example.team_rocket.dto.UserResponse;
import com.example.team_rocket.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponse> getUsers(Authentication authentication) {
        return userService.getAllUsersExcept(authentication.getName());
    }
}
