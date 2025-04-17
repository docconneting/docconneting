package com.example.docconneting.domain.payment.entity;

import com.example.docconneting.domain.order.entity.Order;
import com.example.docconneting.domain.payment.enums.PaymentMethod;
import com.example.docconneting.domain.payment.enums.PaymentStatus;
import com.example.docconneting.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
    @Column(name = "payment_history_id")
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "imp_uid")
    private String impUid;

    private LocalDateTime approvedAt;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private PaymentHistory(User user, Order order, Integer price, PaymentMethod paymentMethod,PaymentStatus paymentStatus, String impUid, LocalDateTime approvedAt) {
        this.user = user;
        this.order = order;
        this.price = price;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.impUid = impUid;
        this.approvedAt = approvedAt;
    }

    public static PaymentHistory of(User user, Order order, Integer price,
                                    PaymentMethod paymentMethod, PaymentStatus paymentStatus,
                                    String impUid, LocalDateTime approvedAt) {
        return new PaymentHistory(user, order, price, paymentMethod, paymentStatus, impUid, approvedAt);
    }
}
