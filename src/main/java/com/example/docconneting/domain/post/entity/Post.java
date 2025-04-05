package com.example.docconneting.domain.post.entity;

import com.example.docconneting.common.base.BaseEntity;
import com.example.docconneting.common.enums.Major;
import com.example.docconneting.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
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

    private Boolean isPaid;

    private Boolean isDeleted;

    private Boolean isReplied;

    private LocalDateTime deadline;

    public Post(User patient, String title, String contents, Major major, Boolean isPaid, Boolean isDeleted, Boolean isReplied, LocalDateTime deadline) {
        this.patient = patient;
        this.title = title;
        this.contents = contents;
        this.major = major;
        this.isPaid = isPaid;
        this.isDeleted = isDeleted;
        this.isReplied = isReplied;
        this.deadline = deadline;
    }

    public void updateTitle(String title){
        this.title = title;
    }

    public void updateContents(String contents){
        this.contents = contents;
    }

    public void delete(){
        isDeleted = true;
    }
}
