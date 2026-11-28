package com.example.team_rocket.service;

import com.example.team_rocket.dto.MessageResponse;
import com.example.team_rocket.dto.SendMessageRequest;
import com.example.team_rocket.entity.Message;
import com.example.team_rocket.entity.User;
import com.example.team_rocket.repository.MessageRepository;
import com.example.team_rocket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageResponse sendMessage(
            String senderUsername,
            SendMessageRequest request) {

        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Sender not found"
                ));

        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Receiver not found"
                ));

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException(
                    "You cannot send a message to yourself"
            );
        }

        String content = request.getContent().trim();

        if (content.isEmpty()) {
            throw new IllegalArgumentException(
                    "Message cannot be empty"
            );
        }

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setSentAt(LocalDateTime.now());

        return convertToResponse(messageRepository.save(message));
    }

    public List<MessageResponse> getConversation(
            String currentUsername,
            Long otherUserId) {

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Current user not found"
                ));

        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found"
                ));

        return messageRepository
                .findBySenderAndReceiverOrSenderAndReceiverOrderBySentAtAsc(
                        currentUser,
                        otherUser,
                        otherUser,
                        currentUser
                )
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private MessageResponse convertToResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getSender().getId(),
                message.getSender().getUsername(),
                message.getReceiver().getId(),
                message.getReceiver().getUsername(),
                message.getContent(),
                message.getSentAt()
        );
    }
}
