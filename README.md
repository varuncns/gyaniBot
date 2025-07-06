# 🤖 GyaniBot
<!-- Logo -->
<p align="center">
  <img src="https://raw.githubusercontent.com/varuncns/varuncns/main/gyaniBot.png" alt="Logo" width="120" />
</p>

![Java](https://img.shields.io/badge/Java-21-blue?logo=java)
![Spring Boot](https://img.shields.io/badge/SpringBoot-3.2.x-brightgreen?logo=spring)

GyaniBot is a pluggable, production-ready chatbot microservice built using **Spring Boot** and **Spring AI**. It exposes a simple REST API that wraps OpenAI GPT models and provides metadata about each response.

## Table of Contents
- [Features](#features)
- [Quick Start](#quick-start)
- [API](#api)
- [Folder Structure](#folder-structure)
- [Tech Stack](#tech-stack)
- [Security & Best Practices](#security--best-practices)
- [Author](#author)

## Features
- REST endpoint to chat with OpenAI models.
- Model configurable via `spring.ai.openai.chat.model`.
- Response metadata includes role, model, message id, finish reason and timestamp.
- Secure endpoints using an `X-API-KEY` header.
- Global exception handling for consistent API errors.
- Stores chat messages in MongoDB and session data in PostgreSQL.
- Retrieve chat history for a session.
- List active chat sessions for a user.
- Context-aware conversations using the last 5 messages.
- Choose from multiple chat personas (WISE_SAGE, FRIENDLY_BUDDY, TECH_EXPERT,
  MOTIVATIONAL_COACH, SARCASTIC_BOT) to tailor the bot's responses.
- API documentation available via Swagger UI at `/swagger-ui.html`.

## Quick Start
1. Clone the repository and `cd` into it.
2. Copy `src/main/resources/application-local.properties.sample` to `src/main/resources/application-local.properties` and set `spring.ai.openai.api-key` and `x-api-key`.
3. Start PostgreSQL and MongoDB:
   ```bash
   docker-compose up -d
   ```
4. Run the service:
   ```bash
   ./mvnw spring-boot:run
   ```
5. Send a message to `http://localhost:8080/api/chat/message` with headers
   `X-API-KEY: your-api-key`, `X-USER-EMAIL: you@example.com` and
   `X-SESSION-ID: <uuid>`. Include an optional `persona` field in the JSON body
   to pick the chat persona.
6. To change the persona later, call `PATCH /api/chat/persona?persona=TECH_EXPERT`
   with the same headers.
7. Open `http://localhost:8080/swagger-ui.html` for API docs.

### Endpoints
| Method | Path | Description |
| ------ | ---- | ----------- |
| POST |/api/chat/message | Send a chat message |
| GET |/api/chat/history | Retrieve previous messages |
| GET |/api/chat/sessions | List all chat sessions |
| PATCH |/api/chat/persona | Update active persona |

### Payloads
#### ChatRequest
```json
{
  "message": "Hello",
  "persona": "FRIENDLY_BUDDY"
}
```

#### ChatResponse
```json
{
  "content": "Hi there!",
  "meta": {
    "role": "ASSISTANT",
    "model": "gpt-3.5-turbo",
    "messageId": "abc123",
    "index": 0,
    "finishReason": "stop",
    "timestamp": "2024-01-01T00:00:00Z"
  },
  "usage": {
    "promptTokens": 0,
    "completionTokens": 0,
    "totalTokens": 0
  }
}
```

#### ChatHistory (array of messages)
```json
[
  {"role": "user", "content": "Hi", "timestamp": "2024-01-01T00:00:00Z"},
  {"role": "assistant", "content": "Hello!", "timestamp": "2024-01-01T00:00:01Z"}
]
```

#### ChatSessionDTO
```json
[
  {"sessionId": "123", "startedAt": "2024-01-01T00:00:00Z", "totalMessages": 2}
]
```

## Folder Structure
```
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com/chatbot/chatbotservice
│   │   │       ├── ChatBotServiceApplication.java
│   │   │       ├── controller
│   │   │       │   └── ChatController.java
│   │   │       ├── dto
│   │   │       │   ├── ChatRequest.java
│   │   │       │   └── ChatResponse.java
│   │   │       ├── exception
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   └── InvalidApiKeyException.java
│   │   │       ├── filter
│   │   │       │   └── ApiKeyFilter.java
│   │   │       ├── entities
│   │   │       │   ├── ChatMessage.java
│   │   │       │   ├── ChatSession.java
│   │   │       │   └── User.java
│   │   │       └── security
│   │   │           └── ApiKeyValidator.java
│   │   └── resources
│   │       └── application.properties
│   └── test
│       └── java/com/chatbot/chatbotservice
│           └── ChatBotServiceApplicationTests.java
```

## Tech Stack
| Category | Technology |
| -------- | ---------- |
| Language | Java 21 |
| Framework | Spring Boot 3.5.3 |
| AI Client | Spring AI (OpenAI ChatModel) |
| Datastores | PostgreSQL, MongoDB |
| Build Tool | Maven |
| Auth | X-API-KEY header |
| Docs | Swagger UI |
## Security & Best Practices
- **Never** commit your `spring.ai.openai.api-key` or other secrets.
- Keep sensitive values in `src/main/resources/application-local.properties` (ignored via `.gitignore`).
- Use `application-local.properties.sample` as a template.

## Author
Built with ❤️ by Varun

