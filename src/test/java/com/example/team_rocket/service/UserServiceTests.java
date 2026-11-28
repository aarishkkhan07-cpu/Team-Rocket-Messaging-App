package com.example.team_rocket.service;

import com.example.team_rocket.dto.UserResponse;
import com.example.team_rocket.entity.User;
import com.example.team_rocket.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTests {

    @Test
    void getAllUsersExceptExcludesCurrentUser() {
        UserRepository repository = mock(UserRepository.class);
        when(repository.findAll()).thenReturn(List.of(
                new User(1L, "alice", "hash"),
                new User(2L, "bob", "hash"),
                new User(3L, "carol", "hash")
        ));
        UserService service = new UserService(repository);

        List<UserResponse> users = service.getAllUsersExcept("alice");

        assertEquals(2, users.size());
        assertEquals("bob", users.get(0).getUsername());
        assertEquals("carol", users.get(1).getUsername());
    }
}
