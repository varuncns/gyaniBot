package com.chatbot.chatbotservice.dto;

import com.chatbot.chatbotservice.enums.ChatPersona;
import lombok.Data;

@Data
public class StartSessionRequest {
    private String userEmail;
    private ChatPersona persona; // optional, default to DEFAULT if not provided
}
