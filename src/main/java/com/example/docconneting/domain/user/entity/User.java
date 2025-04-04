package com.example.docconneting.domain.user.entity;

import com.example.docconneting.common.base.BaseEntity;
import com.example.docconneting.common.enums.Major;
import com.example.docconneting.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String username;

    private Integer point;

    @Enumerated(EnumType.STRING)
    private Major major;

    private String image;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private Boolean isDeleted;

    @Builder
    private User(String email, String password, String username, Integer point, Major major, String image, LocalDateTime startTime, LocalDateTime endTime, Boolean isDeleted, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.point = point;
        this.major = major;
        this.image = image;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isDeleted = isDeleted;
        this.userRole = userRole;
    }
}
