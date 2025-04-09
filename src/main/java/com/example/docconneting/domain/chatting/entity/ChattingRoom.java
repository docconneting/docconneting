package com.example.docconneting.domain.chatting.entity;

import com.example.docconneting.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "chatting_rooms")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private ChattingRoom(User doctor, User patient, Boolean isActive) {
        this.doctor = doctor;
        this.patient = patient;
        this.isActive = isActive;
    }

    public static ChattingRoom of(User doctor, User patient, Boolean isActive){
        return new ChattingRoom(doctor, patient, isActive);
    }

    public void setIsActive(boolean isActive){
        this.isActive = isActive;
    }
}
