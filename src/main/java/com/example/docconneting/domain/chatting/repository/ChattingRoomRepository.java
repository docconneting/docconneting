package com.example.docconneting.domain.chatting.repository;

import com.example.docconneting.domain.chatting.entity.ChattingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long> {

    @Query("SELECT c " +
            "FROM ChattingRoom c " +
            "WHERE c.patient.id = :patientId " +
            "AND c.doctor.id = :doctorId")
    Optional<ChattingRoom> findChattingRoomByPatientAndDoctor(Long patientId, Long doctorId);

    @Query("SELECT c " +
            "FROM ChattingRoom c " +
            "JOIN FETCH c.patient " +
            "JOIN FETCH c.doctor " +
            "WHERE c.id = :chattingRoomId")
    Optional<ChattingRoom> findChattingRoomWithPatientAndDoctor(Long chattingRoomId);
}
