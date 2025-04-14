package com.example.docconneting.domain.post.entity;

import com.example.docconneting.common.base.BaseEntity;
import com.example.docconneting.common.enums.Major;
import com.example.docconneting.domain.post.enums.PayType;
import com.example.docconneting.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts", indexes = {
        @Index(name = "idx_is_deleted_created_at", columnList = "isDeleted, createdAt"),
        @Index(name = "idx_major_is_deleted_created_at", columnList = "major, isDeleted, createdAt"),
        @Index(name = "idx_deadline_payType", columnList = "deadline, payType")
})
@Getter
@NoArgsConstructor
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private User patient;

    private String title;

    private String contents;

    @Enumerated(EnumType.STRING)
    private Major major;

    @Enumerated(EnumType.STRING)
    private PayType payType;

    private Boolean isDeleted;

    private Boolean isReplied;

    private LocalDateTime deadline;

    private Post(User patient, String title, String contents, Major major, PayType payType, Boolean isDeleted, Boolean isReplied, LocalDateTime deadline) {
        this.patient = patient;
        this.title = title;
        this.contents = contents;
        this.major = major;
        this.payType = payType;
        this.isDeleted = isDeleted;
        this.isReplied = isReplied;
        this.deadline = deadline;
    }

    private Post(User patient, String title, String contents, Major major, Boolean isDeleted, Boolean isReplied) {
        this.patient = patient;
        this.title = title;
        this.contents = contents;
        this.major = major;
        this.isDeleted = isDeleted;
        this.isReplied = isReplied;
    }

    public static Post of(User patient, String title, String contents, Major major, PayType payType, Boolean isDeleted, Boolean isReplied, LocalDateTime deadline){
        return new Post(patient, title, contents, major, payType, isDeleted, isReplied, deadline);
    }

    public static Post of(User patient, String title, String contents, Major major, Boolean isDeleted, Boolean isReplied){
        return new Post(patient, title, contents, major, isDeleted, isReplied);
    }

    public void updateTitle(String title){
        this.title = title;
    }

    public void updateContents(String contents){
        this.contents = contents;
    }

    public void updatePayType(PayType payType) {
        this.payType = payType;
    }

    public void delete(){
        isDeleted = true;
    }

    public void changePayTypeAndDeadline() {
        this.payType = PayType.FREE;
        this.deadline = null;
    }

    @PrePersist
    public void setting() {
        this.deadline = this.payType.equals(PayType.POINT) || this.payType.equals(PayType.COUPON) ? this.getCreatedAt().plusDays(1) : null;
    }
}
