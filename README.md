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
- Designed for extension with persistent history and analytics *(planned)*.
- Optional API key security *(planned)*.

## Quick Start
1. Clone the repository and `cd` into it.
2. Export your OpenAI key: `export SPRING_AI_OPENAI_API_KEY=sk-...`.
3. Start the service:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Send a message to `http://localhost:8080/api/chat/message`.

## API
### POST `/api/chat/message`
Request body:
```json
{
  "message": "Hello"
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
│   │   │       └── dto
│   │   │           ├── ChatRequest.java
│   │   │           └── ChatResponse.java
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
- Dockerized PostgreSQL *(planned)*
- REST APIs
- DTO + Service + Repository Architecture
- Lombok
- Secure via `X-API-KEY` *(planned)*

## Author
Built with ❤️ by Varun

