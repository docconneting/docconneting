package com.example.docconneting.domain.order.entity;

import com.example.docconneting.domain.order.enums.OrderProduct;
import com.example.docconneting.domain.order.enums.OrderStatus;
import com.example.docconneting.domain.order.enums.OrderType;
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
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // 생성일 자동 저장 리스너
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

    private String impUid; // 포트원에서 발급한 고유 결제 식별자

    @Column(unique = true, nullable = false)
    private String merchantUid; // 주문 고유 번호 (PG 연동 시 사용)

    private LocalDateTime approvedAt;

    private Long doctorId;

    private Order(User user, OrderType orderType, Integer price, OrderProduct orderProduct, Long doctorId, String merchantUid) {
        this.user = user;
        this.orderType = orderType;
        this.price = price;
        this.orderProduct = orderProduct;
        this.doctorId = doctorId;
        this.merchantUid = merchantUid;
        this.orderStatus = OrderStatus.COMPLETED;
        this.paymentStatus = PaymentStatus.COMPLETED;
    }

    // 포인트 충전 주문 생성
    public static Order ofPointOrder(User user, OrderProduct orderProduct, String merchantUid) {
        return new Order(user, OrderType.POINT, orderProduct.getPrice(), orderProduct, null, merchantUid);
    }

    public static Order ofChatOrder(User user, OrderProduct orderProduct, Long doctorId, String merchantUid) {
        return new Order(user, OrderType.CHAT, orderProduct.getPrice(), orderProduct, doctorId, merchantUid);
    }

    // 결제 성공 후 상태 업데이트
    public void completeOrder(String impUid, PaymentMethod paymentMethod, LocalDateTime approvedAt) {
        this.paymentStatus = PaymentStatus.COMPLETED;
        this.orderStatus = OrderStatus.COMPLETED;
        this.impUid = impUid;
        this.paymentMethod = paymentMethod;
        this.approvedAt = approvedAt;
    }

    // 채팅 주문일 경우 채팅방 id 부여
    public void assignChattingRoomId(Long chattingRoomId) {
        this.chattingRoomId = chattingRoomId;
    }

    public void failOrder() {
        this.orderStatus = OrderStatus.EXPIRED;
        this.paymentStatus = PaymentStatus.FAILED;
    }

    public boolean isCompleted() {
        return OrderStatus.COMPLETED.equals(this.orderStatus);
    }

    public boolean isChatOrder() {
        return OrderType.CHAT.equals(this.orderType);
    }
}