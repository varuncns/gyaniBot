package com.chatbot.chatbotservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

import com.chatbot.chatbotservice.enums.ChatPersona;

@Entity
@Table(name = "chat_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;

    private Instant startedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Enumerated(EnumType.STRING)
    private ChatPersona persona; 
}
