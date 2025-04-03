package com.example.docconneting.domain.user.entity;

import com.example.docconneting.common.base.BaseEntity;
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

    private String major;

    private String image;

    private LocalDateTime start_time;

    private LocalDateTime end_time;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private Boolean isDeleted;

    @Builder
    public User(String email, String password, String username, Integer point, String major, String image, LocalDateTime start_time, LocalDateTime end_time, Boolean isDeleted, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.point = point;
        this.major = major;
        this.image = image;
        this.start_time = start_time;
        this.end_time = end_time;
        this.isDeleted = isDeleted;
        this.userRole = userRole;
    }
}
