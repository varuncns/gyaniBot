package com.chatbot.chatbotservice.service.impl;

import com.chatbot.chatbotservice.document.ChatMessage;
import com.chatbot.chatbotservice.document.ChatMessageDTO;
import com.chatbot.chatbotservice.dto.ChatRequest;
import com.chatbot.chatbotservice.dto.ChatResponse;
import com.chatbot.chatbotservice.dto.ChatResponse.Meta;
import com.chatbot.chatbotservice.dto.ChatResponse.Usage;
import com.chatbot.chatbotservice.entities.ChatSession;
import com.chatbot.chatbotservice.entities.ChatSessionDTO;
import com.chatbot.chatbotservice.entities.User;
import com.chatbot.chatbotservice.enums.ChatPersona;
import com.chatbot.chatbotservice.repository.ChatMessageRepository;
import com.chatbot.chatbotservice.repository.ChatSessionRepository;
import com.chatbot.chatbotservice.repository.UserRepository;
import com.chatbot.chatbotservice.service.ChatService;
import com.chatbot.chatbotservice.service.PersonalPromptFactory;
import jakarta.transaction.Transactional;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ChatServiceImpl implements ChatService {

    private final UserRepository userRepo;
    private final ChatSessionRepository sessionRepo;
    private final ChatMessageRepository chatMessageRepo;
    private final ChatModel chatModel;
    private final PersonalPromptFactory personaPromptFactory;

    @Value("${spring.ai.openai.chat.model}")
    private String modelName;

    public ChatServiceImpl(
            UserRepository userRepo,
            ChatSessionRepository sessionRepo,
            ChatMessageRepository chatMessageRepo,
            ChatModel chatModel,
            PersonalPromptFactory personaPromptFactory
    ) {
        this.userRepo = userRepo;
        this.sessionRepo = sessionRepo;
        this.chatMessageRepo = chatMessageRepo;
        this.chatModel = chatModel;
        this.personaPromptFactory = personaPromptFactory;
    }

    @Override
    public ChatResponse chat(ChatRequest request, String userEmail, String sessionId) {
        // 1. Get or create user
        User user = userRepo.findByEmail(userEmail).orElseGet(() ->
                userRepo.save(User.builder().email(userEmail).build()));

        // 2. Get or create session
        ChatSession session = sessionRepo.findBySessionId(sessionId).orElse(null);

        if (session == null) {
            ChatPersona persona = request.getPersona() != null ? request.getPersona() : ChatPersona.DEFAULT;
            session = sessionRepo.save(ChatSession.builder()
                    .sessionId(sessionId)
                    .startedAt(Instant.now())
                    .user(user)
                    .persona(persona)
                    .build());
        }

        // 3. Save user message
        chatMessageRepo.save(ChatMessage.builder()
                .userEmail(userEmail)
                .sessionId(sessionId)
                .role("user")
                .content(request.getMessage())
                .timestamp(Instant.now())
                .build());

        // 4. Build prompt with system prompt and last 5 messages
        String personaPrompt = personaPromptFactory.getSystemPrompt(session.getPersona());
        SystemMessage systemMessage = new SystemMessage(personaPrompt);

        List<org.springframework.ai.chat.messages.Message> promptMessages = new ArrayList<>();
        promptMessages.add(systemMessage);

        List<ChatMessage> previousMessages = chatMessageRepo
                .findTop5BySessionIdOrderByTimestampAsc(sessionId);

        for (ChatMessage msg : previousMessages) {
            if ("user".equals(msg.getRole())) {
                promptMessages.add(new UserMessage(msg.getContent()));
            } else if ("assistant".equals(msg.getRole())) {
                promptMessages.add(new AssistantMessage(msg.getContent()));
            }
        }

        // Add current message
        promptMessages.add(new UserMessage(request.getMessage()));

        Prompt prompt = new Prompt(promptMessages);
        var result = chatModel.call(prompt);

        // 5. Parse GPT response
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

        // 6. Save assistant reply
        chatMessageRepo.save(ChatMessage.builder()
                .userEmail(userEmail)
                .sessionId(sessionId)
                .role("assistant")
                .content(aiReply)
                .timestamp(Instant.now())
                .build());

        // 7. Return response
        return new ChatResponse(aiReply, meta, usage);
    }

    @Override
    public List<ChatMessageDTO> getChatHistory(String sessionId) {
        return chatMessageRepo.findBySessionIdOrderByTimestampAsc(sessionId).stream()
                .map(msg -> new ChatMessageDTO(msg.getRole(), msg.getContent(), msg.getTimestamp()))
                .toList();
    }

    @Override
    public List<ChatSessionDTO> getAllSessions(String userEmail) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return sessionRepo.findByUser(user).stream().map(session -> {
            int count = chatMessageRepo.countBySessionId(session.getSessionId());
            return new ChatSessionDTO(session.getSessionId(), session.getStartedAt(), count);
        }).toList();
    }

    @Override
    public void updatePersona(String sessionId, ChatPersona persona) {
        ChatSession session = sessionRepo.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        session.setPersona(persona);
        sessionRepo.save(session);
    }
}
