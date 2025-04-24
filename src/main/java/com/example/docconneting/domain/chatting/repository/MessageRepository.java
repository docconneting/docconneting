package com.example.docconneting.domain.chatting.repository;

import com.example.docconneting.domain.chatting.dto.projection.MessageList;
import com.example.docconneting.domain.chatting.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m.user.id AS userId, m.contents AS contents, m.createdAt AS createdAt " +
            "FROM Message m " +
            "WHERE m.chattingRoom.id = :chattingRoomId " +
            "ORDER BY m.createdAt DESC")
    Page<MessageList> findAllMessagesWithUser(Long chattingRoomId, Pageable pageable);

}
