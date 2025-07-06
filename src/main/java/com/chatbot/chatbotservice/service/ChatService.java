package com.chatbot.chatbotservice.service;

import com.chatbot.chatbotservice.dto.ChatRequest;
import com.chatbot.chatbotservice.dto.ChatResponse;
import com.chatbot.chatbotservice.entities.ChatSessionDTO;
import com.chatbot.chatbotservice.enums.ChatPersona;

import java.util.List;

import com.chatbot.chatbotservice.document.*;

public interface ChatService {
    ChatResponse chat(ChatRequest request, String userEmail, String sessionId);
    List<ChatMessageDTO> getChatHistory(String sessionId);
    List<ChatSessionDTO> getAllSessions(String userEmail);
    void updatePersona(String sessionId, ChatPersona persona);
}
