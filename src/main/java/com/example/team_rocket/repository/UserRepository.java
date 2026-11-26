package com.example.team_rocket.repository;

import com.example.team_rocket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;


public interface UserRepository extends JpaRepository<User,Long>{
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    
}
