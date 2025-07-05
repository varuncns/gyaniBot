package com.chatbot.chatbotservice.controller;

import com.chatbot.chatbotservice.dto.ChatRequest;
import com.chatbot.chatbotservice.dto.ChatResponse;
import com.chatbot.chatbotservice.entities.ChatSessionDTO;
import com.chatbot.chatbotservice.service.ChatService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import com.chatbot.chatbotservice.document.ChatMessageDTO;


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
    
    @GetMapping("/history")
    public List<ChatMessageDTO> getHistory(@RequestHeader("X-SESSION-ID") String sessionId) {
        return chatService.getChatHistory(sessionId);
    }
    @GetMapping("/sessions")
    public List<ChatSessionDTO> getSessions(@RequestHeader("X-USER-EMAIL") String userEmail) {
        return chatService.getAllSessions(userEmail);
    }


}
