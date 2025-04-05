package com.example.docconneting.domain.alarm.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "alarms")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private Long fromId;

    private Long toId;

    private Boolean isViewed;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private Alarm(String content, Long fromId, Long toId, Boolean isViewed) {
        this.content = content;
        this.fromId = fromId;
        this.toId = toId;
        this.isViewed = isViewed;
    }
}

