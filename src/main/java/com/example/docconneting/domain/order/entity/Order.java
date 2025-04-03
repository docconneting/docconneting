package com.example.docconneting.domain.order.entity;

import com.example.docconneting.common.base.BaseEntity;
import com.example.docconneting.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private OrderType orderType;

    private Integer price;

    private Long chattingRoomId;

    @Builder
    public Order(User user, OrderType orderType, Integer price, Long chattingRoomId) {
        this.user = user;
        this.orderType = orderType;
        this.price = price;
        this.chattingRoomId = chattingRoomId;
    }
}
