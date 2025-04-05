package com.example.docconneting.domain.order.entity;

import com.example.docconneting.domain.order.enums.OrderProduct;
import com.example.docconneting.domain.order.enums.OrderType;
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
@Table(name = "orders")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    private Integer price;

    private Long chattingRoomId;

    @Enumerated(EnumType.STRING)
    private OrderProduct orderProduct;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private Order(User user, OrderType orderType, Integer price, Long chattingRoomId, OrderProduct orderProduct) {
        this.user = user;
        this.orderType = orderType;
        this.price = price;
        this.chattingRoomId = chattingRoomId;
        this.orderProduct = orderProduct;
    }
}
