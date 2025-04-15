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
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
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

    // PG 연동용 merchantUid 자동 생성
    private String generateMerchantUid() {
        return "order_" + UUID.randomUUID();
    }

    private Order(User user, OrderType orderType, OrderStatus orderStatus,
                  Integer price, Long chattingRoomId, OrderProduct orderProduct, Long doctorId) {
        this.user = user;
        this.orderType = orderType;
        this.orderStatus = orderStatus;
        this.price = price;
        this.chattingRoomId = chattingRoomId;
        this.orderProduct = orderProduct;
        this.doctorId = doctorId;
        this.merchantUid = generateMerchantUid();
    }

    // 포인트 충전 주문 생성
    public static Order ofPointOrder(User user, OrderProduct orderProduct) {
        return new Order(user, OrderType.POINT, OrderStatus.REQUESTED,
                orderProduct.getPrice(), null, orderProduct, null);
    }

    // 채팅 주문 생성
    public static Order ofChatOrder(User user, OrderProduct orderProduct, Long doctorId) {
        return new Order(user, OrderType.CHAT, OrderStatus.REQUESTED,
                orderProduct.getPrice(), null, orderProduct, doctorId);
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

    // 채팅 주문일 경우 채팅방 id 부여
    public void assignChattingRoomId(Long chattingRoomId) {
        this.chattingRoomId = chattingRoomId;
    }
}