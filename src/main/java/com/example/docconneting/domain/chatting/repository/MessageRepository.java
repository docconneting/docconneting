package com.example.docconneting.domain.chatting.repository;

import com.example.docconneting.domain.chatting.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m JOIN FETCH m.user WHERE m.chattingRoom.id = :chattingRoomId ORDER BY m.createdAt DESC")
    Page<Message> findAllMessagesWithUser(Long chattingRoomId, Pageable pageable);
}
