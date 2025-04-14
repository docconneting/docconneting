package com.example.docconneting.domain.order.entity;

import com.example.docconneting.domain.order.enums.OrderProduct;
import com.example.docconneting.domain.order.enums.OrderStatus;
import com.example.docconneting.domain.order.enums.OrderType;
import com.example.docconneting.domain.payment.enums.PaymentMethod;
import com.example.docconneting.domain.payment.enums.PaymentStatus;
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
    private User user;

    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private Integer price;

    private Long chattingRoomId;

    @Enumerated(EnumType.STRING)
    private OrderProduct orderProduct;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // 결제 관련 요약 정보 필드 추가
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String impUid;

    private String merchantUid;

    private LocalDateTime approvedAt;

    private Order(User user, OrderType orderType, OrderStatus orderStatus,
                  Integer price, Long chattingRoomId, OrderProduct orderProduct) {
        this.user = user;
        this.orderType = orderType;
        this.orderStatus = orderStatus;
        this.price = price;
        this.chattingRoomId = chattingRoomId;
        this.orderProduct = orderProduct;
    }

    // 포인트 충전 주문 생성
    public static Order ofPointOrder(User user, OrderProduct orderProduct) {
        return new Order(user, OrderType.POINT, OrderStatus.REQUESTED,
                orderProduct.getPrice(), null, orderProduct);
    }

    // 채팅 주문 생성
    public static Order ofChatOrder(User user, OrderProduct orderProduct) {
        return new Order(user, OrderType.CHAT, OrderStatus.REQUESTED,
                orderProduct.getPrice(), null, orderProduct);
    }

    // 상태 업데이트 메서드들
    public void updatePaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void updateOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void updatePaymentSummary(String impUid, String merchantUid,
                                     PaymentMethod paymentMethod, LocalDateTime approvedAt) {
        this.impUid = impUid;
        this.merchantUid = merchantUid;
        this.paymentMethod = paymentMethod;
        this.approvedAt = approvedAt;
    }

    public void assignChattingRoomId(Long chattingRoomId) {
        this.chattingRoomId = chattingRoomId;
    }
}