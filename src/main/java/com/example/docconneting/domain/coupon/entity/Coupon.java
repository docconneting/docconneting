package com.example.docconneting.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer availableCount;

    private Integer quantity;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Coupon(Integer availableCount, Integer quantity, LocalDateTime startDate, LocalDateTime endDate) {
        this.availableCount = availableCount;
        this.quantity = quantity;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
