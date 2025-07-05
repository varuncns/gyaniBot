package com.chatbot.chatbotservice.repository;

import com.chatbot.chatbotservice.entities.ChatSession;
import com.chatbot.chatbotservice.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    Optional<ChatSession> findBySessionId(String sessionId);
    List<ChatSession> findByUser(User user);

}
