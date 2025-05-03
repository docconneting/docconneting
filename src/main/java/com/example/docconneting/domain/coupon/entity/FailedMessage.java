package com.example.docconneting.domain.coupon.entity;

import com.example.docconneting.domain.coupon.dto.request.CouponIssueRequestMessage;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "failed_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class FailedMessage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long couponId;

    private String errorMessage;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime failedAt;

    private boolean resolved;

//    private FailedMessage(Long couponId, Long userId, String errorMessage, boolean resolved) {
//        this.couponId = couponId;
//        this.userId = userId;
//        this.errorMessage = errorMessage;
//        this.resolved = resolved;
//    }

    public static FailedMessage of(CouponIssueRequestMessage message, String errorMessage) {
        FailedMessage failedMessage = new FailedMessage();
        failedMessage.userId = message.getUserId();
        failedMessage.couponId = message.getCouponId();
        failedMessage.errorMessage = errorMessage;
        failedMessage.resolved = false;
        return failedMessage;
    }

    public void markResolved() {
        this.resolved = true;
    }
}
