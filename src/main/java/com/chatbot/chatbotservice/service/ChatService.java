package com.chatbot.chatbotservice.service;

import com.chatbot.chatbotservice.dto.ChatRequest;
import com.chatbot.chatbotservice.dto.ChatResponse;

public interface ChatService {
    ChatResponse chat(ChatRequest request, String userEmail, String sessionId);
}
