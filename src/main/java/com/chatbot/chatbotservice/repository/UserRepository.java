package com.chatbot.chatbotservice.repository;

import com.chatbot.chatbotservice.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
