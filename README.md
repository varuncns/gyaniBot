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
- Choose from multiple chat personas (WISE_SAGE, FRIENDLY_BUDDY, TECH_EXPERT,
  MOTIVATIONAL_COACH, SARCASTIC_BOT) to tailor the bot's responses.
- API documentation available via Swagger UI at `/swagger-ui.html`.

## Quick Start
1. Clone the repository and `cd` into it.
2. Start PostgreSQL and MongoDB:
   ```bash
   docker-compose up -d
   ```
3. Export your OpenAI key: `export SPRING_AI_OPENAI_API_KEY=sk-...`.
4. Set the expected API key (used in the `X-API-KEY` header): `export X_API_KEY=my-secret`.
5. Start the service:
   ```bash
   ./mvnw spring-boot:run
   ```
6. Send a message to `http://localhost:8080/api/chat/message` with headers
   `X-API-KEY: my-secret`, `X-USER-EMAIL: you@example.com` and
   `X-SESSION-ID: <uuid>`. Include an optional `persona` field in the JSON body
   to pick the chat persona.
7. To change the persona later, call `PATCH /api/chat/persona?persona=TECH_EXPERT`
   with the same headers.
8. Open `http://localhost:8080/swagger-ui.html` for API docs.

## API
### POST `/api/chat/message`
Request body:
```json
{
  "message": "Hello",
  "persona": "FRIENDLY_BUDDY" // optional
}
```
Example response:
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

### GET `/api/chat/history`
Headers: `X-SESSION-ID`
```json
[
  { "role": "user", "content": "Hi", "timestamp": "2024-01-01T00:00:00Z" },
  { "role": "assistant", "content": "Hello!", "timestamp": "2024-01-01T00:00:01Z" }
]
```

### GET `/api/chat/sessions`
Headers: `X-USER-EMAIL`
```json
[
  { "sessionId": "123", "startedAt": "2024-01-01T00:00:00Z", "totalMessages": 2 }
]
```

### PATCH `/api/chat/persona`
Query parameter: `persona`
Headers: `X-USER-EMAIL`, `X-SESSION-ID`

```
curl -X PATCH \
  'http://localhost:8080/api/chat/persona?persona=TECH_EXPERT' \
  -H 'X-API-KEY: my-secret' \
  -H 'X-USER-EMAIL: you@example.com' \
  -H 'X-SESSION-ID: <uuid>'
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
- Java 21
- Spring Boot 3.5.3
- Spring AI (OpenAI ChatModel)
- Dockerized PostgreSQL and MongoDB
- REST APIs
- DTO + Service + Repository Architecture
- Lombok
- Secure via `X-API-KEY`
- Swagger UI

## Author
Built with ❤️ by Varun

