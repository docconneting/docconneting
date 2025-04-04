package com.example.docconneting.domain.chatting.repository;

import com.example.docconneting.domain.chatting.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
