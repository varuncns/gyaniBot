package com.chatbot.chatbotservice.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "chat_messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    @Id
    private String id;
    private String sessionId;
    private String userEmail;
    private String role; // "user" or "assistant"
    private String content;
    private Instant timestamp;
}
