package com.example.docconneting.domain.point.entity;

import com.example.docconneting.domain.point.enums.PointType;
import com.example.docconneting.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_histories")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PointHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Long postId;

    private Boolean isRefunded;

    @Enumerated(EnumType.STRING)
    private PointType pointType;

    private Integer point;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private PointHistory(User user, Long postId, Boolean isRefunded, PointType pointType, Integer point) {
        this.user = user;
        this.postId = postId;
        this.isRefunded = isRefunded;
        this.pointType = pointType;
        this.point = point;
    }

    public static PointHistory of(User user, Long postId, Boolean isRefunded, PointType pointType, Integer point) {
        return new PointHistory(user, postId, isRefunded, pointType, point);
    }
}
