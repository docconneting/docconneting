package com.example.docconneting.domain.payment.entity;

import com.example.docconneting.domain.order.entity.Order;
import com.example.docconneting.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class PaymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private Integer price;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String payment_key;

    private LocalDateTime approvedAt;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder

    public PaymentHistory(User user, Order order, Integer price, PaymentStatus paymentStatus, String payment_key, LocalDateTime approvedAt, LocalDateTime createdAt) {
        this.user = user;
        this.order = order;
        this.price = price;
        this.paymentStatus = paymentStatus;
        this.payment_key = payment_key;
        this.approvedAt = approvedAt;
        this.createdAt = createdAt;
    }
}
