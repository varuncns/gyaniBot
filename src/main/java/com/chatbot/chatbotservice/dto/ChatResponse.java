package com.chatbot.chatbotservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    private String content;
    private Meta meta;
    private Usage usage;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Meta {
        private String role;
        private String model;
        private String messageId;
        private int index;
        private String finishReason;
        private String timestamp;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Usage {
        private int promptTokens;
        private int completionTokens;
        private int totalTokens;
    }
}

