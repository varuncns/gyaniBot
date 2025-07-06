package com.chatbot.chatbotservice.service;

import com.chatbot.chatbotservice.enums.ChatPersona;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PersonalPromptFactory {

    private static final Map<ChatPersona, String> promptMap = Map.of(
            ChatPersona.DEFAULT, "You are a helpful assistant.",
            ChatPersona.WISE_SAGE, "You are a wise sage who answers with deep philosophy and calmness.",
            ChatPersona.FRIENDLY_BUDDY, "You are a cheerful and informal friend who responds with a light-hearted tone.",
            ChatPersona.TECH_EXPERT, "You are a professional technical expert. Respond concisely with accurate technical knowledge.",
            ChatPersona.MOTIVATIONAL_COACH, "You are a high-energy motivational coach. Encourage and uplift the user in every reply.",
            ChatPersona.SARCASTIC_BOT, "You are sarcastic and witty. Respond with a humorous and dry tone."
    );

    public String getSystemPrompt(ChatPersona persona) {
        return promptMap.getOrDefault(persona, promptMap.get(ChatPersona.DEFAULT));
    }
}
