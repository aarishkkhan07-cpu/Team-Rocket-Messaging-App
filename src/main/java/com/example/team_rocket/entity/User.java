package com.example.team_rocket.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users",
    uniqueConstraints={
        @UniqueConstraint(
            columnNames="username"
        )
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id 
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(
        nullable = false,
        unique = true,
        length = 50
    )
    private String username;

    @Column(
        nullable=false
    )
    private String password;

    
}
