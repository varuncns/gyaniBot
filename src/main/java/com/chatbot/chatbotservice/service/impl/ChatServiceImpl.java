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
import com.chatbot.chatbotservice.service.PersonalPromptFactory;
import com.chatbot.chatbotservice.repository.ChatMessageRepository;
import com.chatbot.chatbotservice.repository.ChatSessionRepository;
import com.chatbot.chatbotservice.repository.UserRepository;
import com.chatbot.chatbotservice.service.ChatService;
import jakarta.transaction.Transactional;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
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

        // 3. Save user message in MongoDB
        chatMessageRepo.save(ChatMessage.builder()
                .userEmail(userEmail)
                .sessionId(sessionId)
                .role("user")
                .content(request.getMessage())
                .timestamp(Instant.now())
                .build());

        // 4. Generate GPT response with system prompt based on persona
        String personaPrompt = personaPromptFactory.getSystemPrompt(session.getPersona());
        SystemMessage systemMessage = new SystemMessage(personaPrompt);
        UserMessage userMessage = new UserMessage(request.getMessage());

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
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

    @Override
    public List<ChatMessageDTO> getChatHistory(String sessionId) {
        List<ChatMessage> messages = chatMessageRepo.findBySessionIdOrderByTimestampAsc(sessionId);
        return messages.stream()
                .map(msg -> new ChatMessageDTO(msg.getRole(), msg.getContent(), msg.getTimestamp()))
                .toList();
    }

    @Override
    public List<ChatSessionDTO> getAllSessions(String userEmail) {
        // 1. Get user
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Get sessions
        List<ChatSession> sessions = sessionRepo.findByUser(user);

        // 3. Build DTOs with Mongo count
        return sessions.stream().map(session -> {
            int count = chatMessageRepo.countBySessionId(session.getSessionId());
            return new ChatSessionDTO(
                    session.getSessionId(),
                    session.getStartedAt(),
                    count
            );
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
