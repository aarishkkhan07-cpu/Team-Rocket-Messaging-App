package com.example.team_rocket.service;

import com.example.team_rocket.dto.MessageResponse;
import com.example.team_rocket.dto.SendMessageRequest;
import com.example.team_rocket.entity.Message;
import com.example.team_rocket.entity.User;
import com.example.team_rocket.repository.MessageRepository;
import com.example.team_rocket.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MessageServiceTests {

    private MessageRepository messageRepository;
    private UserRepository userRepository;
    private MessageService messageService;
    private User alice;
    private User bob;

    @BeforeEach
    void setUp() {
        messageRepository = mock(MessageRepository.class);
        userRepository = mock(UserRepository.class);
        messageService = new MessageService(messageRepository, userRepository);
        alice = new User(1L, "alice", "hash");
        bob = new User(2L, "bob", "hash");
    }

    @Test
    void sendMessageTrimsContentAndReturnsResponse() {
        SendMessageRequest request = sendRequest(2L, "  hello bob  ");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(alice));
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            message.setId(10L);
            return message;
        });

        MessageResponse response = messageService.sendMessage("alice", request);

        assertEquals(10L, response.getId());
        assertEquals("alice", response.getSenderUsername());
        assertEquals("bob", response.getReceiverUsername());
        assertEquals("hello bob", response.getContent());
    }

    @Test
    void sendMessageRejectsSelfMessage() {
        SendMessageRequest request = sendRequest(1L, "hello");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(alice));
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> messageService.sendMessage("alice", request));

        assertEquals("You cannot send a message to yourself", exception.getMessage());
        verify(messageRepository, never()).save(any());
    }

    @Test
    void sendMessageRejectsEmptyContent() {
        SendMessageRequest request = sendRequest(2L, "   ");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(alice));
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> messageService.sendMessage("alice", request));

        assertEquals("Message cannot be empty", exception.getMessage());
        verify(messageRepository, never()).save(any());
    }

    @Test
    void getConversationReturnsRepositoryMessagesInResponseOrder() {
        LocalDateTime sentAt = LocalDateTime.of(2026, 11, 28, 10, 0);
        Message message = new Message(20L, alice, bob, "hello", sentAt);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(alice));
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));
        when(messageRepository.findBySenderAndReceiverOrSenderAndReceiverOrderBySentAtAsc(
                alice, bob, bob, alice)).thenReturn(List.of(message));

        List<MessageResponse> responses = messageService.getConversation("alice", 2L);

        assertEquals(1, responses.size());
        assertEquals("hello", responses.get(0).getContent());
        assertEquals(sentAt, responses.get(0).getSentAt());
    }

    private SendMessageRequest sendRequest(Long receiverId, String content) {
        SendMessageRequest request = new SendMessageRequest();
        request.setReceiverId(receiverId);
        request.setContent(content);
        return request;
    }
}
