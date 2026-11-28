package com.example.team_rocket.repository;

import com.example.team_rocket.entity.Message;
import com.example.team_rocket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderAndReceiverOrSenderAndReceiverOrderBySentAtAsc(
            User sender1,
            User receiver1,
            User sender2,
            User receiver2
    );
}
