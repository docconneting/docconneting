package com.example.docconneting.domain.chatting.repository;

import com.example.docconneting.domain.chatting.entity.ChattingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long> {
}
