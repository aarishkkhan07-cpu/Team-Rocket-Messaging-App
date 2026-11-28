package com.example.team_rocket.service;

import com.example.team_rocket.dto.UserResponse;
import com.example.team_rocket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsersExcept(String currentUsername) {
        return userRepository.findAll()
                .stream()
                .filter(user ->
                        !user.getUsername().equals(currentUsername))
                .map(user ->
                        new UserResponse(
                                user.getId(),
                                user.getUsername()
                        ))
                .toList();
    }
}
