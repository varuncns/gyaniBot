package com.chatbot.chatbotservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyValidator {

    @Value("${x-api-key}")
    private String expectedApiKey;

    public boolean isValid(String apiKey) {
        return expectedApiKey.equals(apiKey);
    }
}

