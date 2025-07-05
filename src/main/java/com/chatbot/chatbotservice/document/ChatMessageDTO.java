package com.chatbot.chatbotservice.document;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ChatMessageDTO {
    private String role;       // "user" or "assistant"
    private String content;
    private Instant timestamp;
}
