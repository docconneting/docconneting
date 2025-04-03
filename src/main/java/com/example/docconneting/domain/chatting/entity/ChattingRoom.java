package com.example.docconneting.domain.chatting.entity;

import com.example.docconneting.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chatting_rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChattingRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private User doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private User patient;

    private Boolean isActive;

    private LocalDateTime created_at;

    @Builder
    public ChattingRoom(User doctor, User patient, Boolean isActive, LocalDateTime created_at) {
        this.doctor = doctor;
        this.patient = patient;
        this.isActive = isActive;
        this.created_at = created_at;
    }
}
