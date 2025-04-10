package com.example.docconneting.domain.order.entity;

import com.example.docconneting.domain.order.enums.OrderProduct;
import com.example.docconneting.domain.order.enums.OrderStatus;
import com.example.docconneting.domain.order.enums.OrderType;
import com.example.docconneting.domain.user.entity.User;
import jakarta.persistence.*;
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
    private User user; // 주문자 이름

    @Enumerated(EnumType.STRING)
    private OrderType orderType; // 주문 방식

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문 상태

    private Integer price; // 가격

    private Long chattingRoomId; // 채팅방 Id

    @Enumerated(EnumType.STRING)
    private OrderProduct orderProduct; // 주문 상품

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt; // 주문 시작

    private Order(User user, OrderType orderType, OrderStatus orderStatus, Integer price, Long chattingRoomId, OrderProduct orderProduct) {
        this.user = user;
        this.orderType = orderType;
        this.orderStatus = orderStatus;
        this.price = price;
        this.chattingRoomId = chattingRoomId;
        this.orderProduct = orderProduct;
    }

    // 포인트 충전
    public static Order ofPointOrder(User user, OrderProduct orderProduct) {
        return new Order(user, OrderType.POINT, OrderStatus.REQUESTED, orderProduct.getPrice(), null, orderProduct);
    }

    // 채팅 결제
    public static Order ofChatOrder(User user, OrderProduct orderProduct) {
        return new Order(user, OrderType.CHAT, OrderStatus.REQUESTED, orderProduct.getPrice(), null, orderProduct);
    }
}
