package com.chatbot.chatbotservice.controller;

import com.chatbot.chatbotservice.dto.ChatRequest;
import com.chatbot.chatbotservice.dto.ChatResponse;
import com.chatbot.chatbotservice.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/message")
    public ChatResponse chat(
            @RequestHeader("X-API-KEY") String apiKey,
            @RequestHeader("X-USER-EMAIL") String email,
            @RequestHeader("X-SESSION-ID") String sessionId,
            @RequestBody ChatRequest request
    ) {
        return chatService.chat(request, email, sessionId);
    }
}
