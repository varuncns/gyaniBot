package com.chatbot.chatbotservice.controller;

import com.chatbot.chatbotservice.dto.ChatRequest;
import com.chatbot.chatbotservice.dto.ChatResponse;
import com.chatbot.chatbotservice.dto.ChatResponse.Meta;
import com.chatbot.chatbotservice.dto.ChatResponse.Usage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatModel chatModel;

    @Value("${spring.ai.openai.chat.model}")
    private String modelName;

    public ChatController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @PostMapping("/message")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        UserMessage userMessage = new UserMessage(request.getMessage());
        Prompt prompt = new Prompt(List.of(userMessage));
        var result = chatModel.call(prompt);

        if (result.getResult().getOutput() instanceof AssistantMessage assistantMessage) {
            String content = assistantMessage.getText();

            Map<String, Object> metadata = assistantMessage.getMetadata();
            String finishReason = (String) metadata.getOrDefault("finishReason", "UNKNOWN");
            String messageId = (String) metadata.getOrDefault("id", "N/A");
            int index = (int) metadata.getOrDefault("index", 0);
            String role = (String) metadata.getOrDefault("role", "ASSISTANT");

            Meta meta = new Meta(
                    role,
                    modelName,
                    messageId,
                    index,
                    finishReason,
                    Instant.now().toString()
            );

            Usage usage = new Usage(0, 0, 0);

            return new ChatResponse(content, meta, usage);
        }

        Meta fallbackMeta = new Meta("UNKNOWN", modelName, "N/A", -1, "ERROR", Instant.now().toString());
        Usage fallbackUsage = new Usage(0, 0, 0);
        return new ChatResponse("No response", fallbackMeta, fallbackUsage);
    }
}
