package com.example.docconneting.domain.order.repository;

import com.example.docconneting.domain.order.entity.Order;
import com.example.docconneting.domain.order.enums.OrderStatus;
import com.example.docconneting.domain.order.enums.OrderType;
import com.example.docconneting.domain.payment.enums.PaymentStatus;
import com.example.docconneting.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUser(User user, Pageable pageable);
    Optional<Order> findByMerchantUid(String merchantUid);

    // 채팅 주문 상태가 COMPLETED이고, chattingRoomId가 아직 비어있는 주문들 중 가장 최근 주문 하나 조회
    @Query("""
    SELECT o FROM Order o
    WHERE o.user = :user
      AND o.orderType = :orderType
      AND o.orderStatus = :orderStatus
      AND o.chattingRoomId IS NULL
    ORDER BY o.createdAt DESC
""")
    Optional<Order> findLatestCompletedChatOrder(@Param("user") User user,
                                                 @Param("orderType") OrderType orderType,
                                                 @Param("orderStatus") OrderStatus orderStatus);

    @Query("""
    SELECT o FROM Order o
    WHERE o.orderStatus = :orderStatus
      AND o.paymentStatus = :paymentStatus
      AND o.createdAt <= :threshold
""")
    List<Order> findAllByStatusBeforeTime(@Param("orderStatus") OrderStatus orderStatus,
                                          @Param("paymentStatus") PaymentStatus paymentStatus,
                                          @Param("threshold") LocalDateTime threshold);
}