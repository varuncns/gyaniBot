package com.chatbot.chatbotservice.entities;

import java.time.Instant;

public record ChatSessionDTO(
    String sessionId,
    Instant startedAt,
    int totalMessages
) {}
