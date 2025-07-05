package com.chatbot.chatbotservice.repository;

import com.chatbot.chatbotservice.document.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findBySessionId(String sessionId);
}
