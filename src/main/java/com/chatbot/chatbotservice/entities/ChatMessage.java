package com.chatbot.chatbotservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role; // "user" or "assistant"
    private String content;
    private Instant timestamp;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private ChatSession session;
}

