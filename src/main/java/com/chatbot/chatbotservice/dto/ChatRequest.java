package com.chatbot.chatbotservice.dto;

import com.chatbot.chatbotservice.enums.ChatPersona;

import lombok.Data;

@Data
public class ChatRequest {
    private String message;
    private ChatPersona persona;
}
