package com.chatbot.chatbotservice.service.impl;

import com.chatbot.chatbotservice.document.ChatMessage;
import com.chatbot.chatbotservice.dto.ChatRequest;
import com.chatbot.chatbotservice.dto.ChatResponse;
import com.chatbot.chatbotservice.dto.ChatResponse.Meta;
import com.chatbot.chatbotservice.dto.ChatResponse.Usage;
import com.chatbot.chatbotservice.entities.ChatSession;
import com.chatbot.chatbotservice.entities.User;
import com.chatbot.chatbotservice.repository.ChatMessageRepository;
import com.chatbot.chatbotservice.repository.ChatSessionRepository;
import com.chatbot.chatbotservice.repository.UserRepository;
import com.chatbot.chatbotservice.service.ChatService;
import jakarta.transaction.Transactional;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ChatServiceImpl implements ChatService {

    private final UserRepository userRepo;
    private final ChatSessionRepository sessionRepo;
    private final ChatMessageRepository chatMessageRepo;
    private final ChatModel chatModel;

    @Value("${spring.ai.openai.chat.model}")
    private String modelName;

    public ChatServiceImpl(
            UserRepository userRepo,
            ChatSessionRepository sessionRepo,
            ChatMessageRepository chatMessageRepo,
            ChatModel chatModel
    ) {
        this.userRepo = userRepo;
        this.sessionRepo = sessionRepo;
        this.chatMessageRepo = chatMessageRepo;
        this.chatModel = chatModel;
    }

    @Override
    public ChatResponse chat(ChatRequest request, String userEmail, String sessionId) {
        // 1. Get or create user
        User user = userRepo.findByEmail(userEmail).orElseGet(() ->
                userRepo.save(User.builder().email(userEmail).build()));

        // 2. Get or create session
        ChatSession session = sessionRepo.findBySessionId(sessionId).orElseGet(() ->
                sessionRepo.save(ChatSession.builder()
                        .sessionId(sessionId)
                        .startedAt(Instant.now())
                        .user(user)
                        .build()));

        // 3. Save user message in MongoDB
        chatMessageRepo.save(ChatMessage.builder()
                .userEmail(userEmail)
                .sessionId(sessionId)
                .role("user")
                .content(request.getMessage())
                .timestamp(Instant.now())
                .build());

        // 4. Generate GPT response
        Prompt prompt = new Prompt(List.of(new UserMessage(request.getMessage())));
        var result = chatModel.call(prompt);

        String aiReply = "Sorry, I couldn’t generate a response.";
        Meta meta = null;
        Usage usage = new Usage(0, 0, 0);

        if (result.getResult().getOutput() instanceof AssistantMessage assistantMessage) {
            aiReply = assistantMessage.getText();
            Map<String, Object> metadata = assistantMessage.getMetadata();

            meta = new Meta(
                    (String) metadata.getOrDefault("role", "ASSISTANT"),
                    modelName,
                    (String) metadata.getOrDefault("id", "N/A"),
                    (int) metadata.getOrDefault("index", 0),
                    (String) metadata.getOrDefault("finishReason", "UNKNOWN"),
                    Instant.now().toString()
            );
        }

        // 5. Save assistant reply in MongoDB
        chatMessageRepo.save(ChatMessage.builder()
                .userEmail(userEmail)
                .sessionId(sessionId)
                .role("assistant")
                .content(aiReply)
                .timestamp(Instant.now())
                .build());

        // 6. Return DTO
        return new ChatResponse(aiReply, meta, usage);
    }
}
