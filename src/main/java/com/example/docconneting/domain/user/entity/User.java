package com.example.docconneting.domain.user.entity;

import com.example.docconneting.common.base.BaseEntity;
import com.example.docconneting.common.enums.Major;
import com.example.docconneting.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "users",
        indexes = {@Index(name = "idx_is_deleted", columnList = "isDeleted"),
                @Index(name = "idx_major_is_deleted", columnList = "major, isDeleted")}
)
@Getter
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String username;

    private Integer point;

    @Enumerated(EnumType.STRING)
    private Major major;

    private String image;

    private LocalTime startTime;

    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private Boolean isDeleted;

    private String fcmToken;

    private Boolean isAlarmEnabled;


    // 환자 생성자
    private User(String email, String password, String username, Integer point, Boolean isDeleted, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.point = point;
        this.isDeleted = isDeleted;
        this.userRole = userRole;
    }

    // 환자 생성 메서드
    public static User of(String email, String password, String username, Integer point, Boolean isDeleted, UserRole userRole) {
        return new User(email, password, username, point, isDeleted, userRole);
    }

    // 의사 생성자
    private User(String email, String password, String username, Major major, String image, LocalTime startTime, LocalTime endTime, Boolean isDeleted, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.major = major;
        this.image = image;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isDeleted = isDeleted;
        this.userRole = userRole;
    }

    // 의사 생성 메서드
    public static User of(String email, String password, String username, Major major, String image, LocalTime startTime, LocalTime endTime, Boolean isDeleted, UserRole userRole) {
        return new User(email, password, username, major, image, startTime, endTime, isDeleted, userRole);
    }

    //비밀번호 setter
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    //의사 이미지 setter
    public void updateImage(String newImage) {
        this.image = newImage;
    }

    // fcm 토큰을 업데이트하는 메서드
    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    // 알람 권한을 업데이트하는 메서드
    public void updateAlarmInfo(String fcmToken, Boolean isAlarmEnabled) {
        this.fcmToken = fcmToken;
        this.isAlarmEnabled = isAlarmEnabled;
    }

}
